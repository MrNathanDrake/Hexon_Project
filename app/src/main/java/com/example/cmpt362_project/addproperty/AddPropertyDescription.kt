package com.example.cmpt362_project.addproperty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cmpt362_project.databinding.AddPropertyDescriptionBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class AddPropertyDescription : AppCompatActivity() {

    private lateinit var binding: AddPropertyDescriptionBinding
    private val openAiApiKey =  "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"
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddPropertyDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.DescriptionToolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }
        binding.DescriptionToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val houseId = intent.getStringExtra("houseId") ?: return
        val address = intent.getStringExtra("address") ?: ""
        val city = intent.getStringExtra("city") ?: ""
        val province = intent.getStringExtra("province") ?: ""
        val postalCode = intent.getStringExtra("postalCode") ?: ""
        val squareFootage = intent.getStringExtra("squareFootage") ?: ""
        val rent = intent.getStringExtra("rent") ?: ""
        val houseKind = intent.getStringExtra("houseKind") ?: ""
        val bedrooms = intent.getStringExtra("bedrooms") ?: ""
        val baths = intent.getStringExtra("baths") ?: ""
        val features = intent.getSerializableExtra("features") as? Map<String, Boolean> ?: emptyMap()

        Log.d("AddPropertyDescription", "address: $address")
        Log.d("AddPropertyDescription", "city: $city")
        Log.d("AddPropertyDescription", "postalCode: $postalCode")
        Log.d("AddPropertyDescription", "squareFootage: $squareFootage")
        Log.d("AddPropertyDescription", "rent: $rent")
        Log.d("AddPropertyDescription", "baths: $baths")
        generateTitleAndDescription(
            address, city, province, postalCode,
            squareFootage, rent, houseKind,
            bedrooms, baths, features
        )

        mDbRef = FirebaseDatabase.getInstance().reference

        binding.next2StepButton.setOnClickListener {
            val title = binding.titleText.text.toString().trim()
            val description = binding.descriptionText.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                mDbRef.child("houses").child(houseId).child("description").setValue(description)
                    .addOnSuccessListener {
                        val intent = Intent(this, AddPropertyImage::class.java)
                        intent.putExtra("houseId", houseId)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Failed to update description", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in both fields before proceeding.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun generateTitleAndDescription(
        address: String, city: String, province: String, postalCode: String,
        squareFootage: String, rent: String, houseKind: String,
        bedrooms: String, baths: String, features: Map<String, Boolean>
    ) {
        val prompt = """
            Generate a property title and description based on the following details:
            Address: $address, $city, $province, $postalCode
            Type: $houseKind
            Bedrooms: $bedrooms
            Bathrooms: $baths
            Square Footage: $squareFootage sqft
            Rent: $$rent/month
            Features: ${features.entries.joinToString(", ") { "${it.key}=${it.value}" }}
            
            Respond with only the title and description, separated by two newlines.
        """.trimIndent()

        val url = "https://api.openai.com/v1/chat/completions"
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("model", "gpt-3.5-turbo")
        json.put("messages", JSONArray().apply {
            put(JSONObject().apply {
                put("role", "system")
                put("content", "You are a helpful assistant that generates property descriptions.")
            })
            put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
        })
        json.put("max_tokens", 250)
        json.put("temperature", 0.7)

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            json.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $openAiApiKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(
                        this@AddPropertyDescription,
                        "Failed to generate description. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody ?: "{}")
                    val generatedText = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim()

                    val splitText = generatedText.split("\n\n")
                    val title = splitText.getOrNull(0) ?: "Generated Title"
                    val description = splitText.getOrNull(1) ?: "Generated Description"

                    runOnUiThread {
                        binding.titleText.setText(title)
                        binding.descriptionText.setText(description)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@AddPropertyDescription,
                            "Error generating description.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
