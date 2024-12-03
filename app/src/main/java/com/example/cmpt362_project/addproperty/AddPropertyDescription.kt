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

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.refreshButton.setOnClickListener {
            binding.titleText.text.clear()
            binding.descriptionText.text.clear()

            // Regenerate content if no title or description exists
            generateTitleAndDescription(
                address = intent.getStringExtra("address") ?: "",
                city = intent.getStringExtra("city") ?: "",
                province = intent.getStringExtra("province") ?: "",
                postalCode = intent.getStringExtra("postalCode") ?: "",
                squareFootage = intent.getStringExtra("squareFootage") ?: "",
                rent = intent.getStringExtra("rent") ?: "",
                houseKind = intent.getStringExtra("houseKind") ?: "",
                bedrooms = intent.getStringExtra("bedrooms") ?: "",
                baths = intent.getStringExtra("baths") ?: "",
                features = intent.getSerializableExtra("features") as? Map<String, Boolean> ?: emptyMap()
            )
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
        val status = intent.getStringExtra("status")?: ""
        val features = intent.getSerializableExtra("features") as? Map<String, Boolean> ?: emptyMap()

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

                val intent = Intent(this, AddPropertyImage::class.java).apply {
                    putExtra("houseId", houseId)
                    putExtra("address", address)
                    putExtra("city", city)
                    putExtra("province", province)
                    putExtra("postalCode", postalCode)
                    putExtra("squareFootage", squareFootage)
                    putExtra("rent", rent)
                    putExtra("houseKind", houseKind)
                    putExtra("bedrooms", bedrooms)
                    putExtra("baths", baths)
                    putExtra("description", description)
                    putExtra("status", status)
                    putExtra("features", HashMap(features))
                }
                startActivity(intent)
            }
        }
    }

    private fun generateTitleAndDescription(
        address: String, city: String, province: String, postalCode: String,
        squareFootage: String, rent: String, houseKind: String,
        bedrooms: String, baths: String, features: Map<String, Boolean>
    ) {
        val prompt = """
        Using the same property details below, generate a new and unique version of the property details:
        Address: $address, $city, $province, $postalCode
        Type: $houseKind
        Bedrooms: $bedrooms
        Bathrooms: $baths
        Square Footage: $squareFootage sqft
        Rent: $$rent/month
        Features: ${features.entries.joinToString(", ") { "${it.key}=${it.value}" }}
        
        Respond with the following format:
        - Title: <Property Title>
        - Description: <Property Description>
    """.trimIndent()

        val url = "https://api.openai.com/v1/chat/completions"
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("model", "gpt-3.5-turbo")
        json.put("messages", JSONArray().apply {
            put(JSONObject().apply {
                put("role", "system")
                put("content", "You are a helpful assistant that generates property details.")
            })
            put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
        })
        json.put("max_tokens", 300)
        json.put("temperature", 0.9)

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
                        "Failed to generate content. Please try again.",
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

                    // Parse the response correctly
                    val titleRegex = Regex("(?<=- Title: ).*")
                    val descriptionRegex = Regex("(?<=- Description: ).*")

                    val title = titleRegex.find(generatedText)?.value?.trim() ?: "Generated Title"
                    val description = descriptionRegex.find(generatedText)?.value?.trim() ?: "Generated Description"

                    runOnUiThread {
                        binding.titleText.setText(title)
                        binding.descriptionText.setText(description)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@AddPropertyDescription,
                            "Error generating content.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}