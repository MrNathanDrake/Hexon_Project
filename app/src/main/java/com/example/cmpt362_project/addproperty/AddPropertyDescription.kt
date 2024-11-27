package com.example.cmpt362_project.addproperty

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cmpt362_project.MainActivity
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
    private val openAiApiKey =
       "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"
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddPropertyDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.DescriptionToolbar)
        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }
        binding.DescriptionToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Populate fields with OpenAI-generated content
        generateTitleAndDescription()

        mDbRef = FirebaseDatabase.getInstance().reference
        val houseId = intent.getStringExtra("houseId") ?: return

        // Handle Next Step button click
        binding.next2StepButton.setOnClickListener {
            val title = binding.titleText.text.toString().trim()
            val description = binding.descriptionText.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                Toast.makeText(this, "Proceeding to the next step...", Toast.LENGTH_SHORT).show()
                // You can pass the data to the next activity or handle navigation here

                mDbRef.child("houses").child(houseId).child("description").setValue(description)
                    .addOnSuccessListener {
                        // 返回Dashboard页面
                        val intent = Intent(this, MainActivity::class.java)
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

    private fun generateTitleAndDescription() {
        val prompt = """
        Generate a property title and description for a spacious 4-bedroom house with central parking in Ottawa. Details:
        - 4 bedrooms
        - 3 bathrooms
        - 2,500 sqft
        - Rent: $4,500/month
        - Central parking, pet-friendly, and EV charger
        
        Respond with only the title and description, separated by two newlines. Do not include prefixes like "Title:" or "Description:".
    """.trimIndent()

        val url = "https://api.openai.com/v1/chat/completions" // Updated endpoint
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("model", "gpt-3.5-turbo") // Or "gpt-4" if supported
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
        json.put("max_tokens", 150)
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

        Log.d("OpenAI", "Sending Request: $json")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OpenAI", "API request failed: ${e.message}")
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
                    Log.d("OpenAI", "Response Body: $responseBody")
                    val jsonResponse = JSONObject(responseBody ?: "{}")
                    val generatedText = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim()

                    // Assume the generated text is in "Title\n\nDescription" format
                    val splitText = generatedText.split("\n\n")
                    val title = splitText.getOrNull(0) ?: "Generated Title"
                    val description = splitText.getOrNull(1) ?: "Generated Description"

                    runOnUiThread {
                        binding.titleText.setText(title)
                        binding.descriptionText.setText(description)
                    }
                } else {
                    Log.e("OpenAI", "API request failed with code: ${response.code}")
                    Log.e("OpenAI", "Error Body: ${response.body?.string()}")
                    runOnUiThread {
                        Toast.makeText(
                            this@AddPropertyDescription,
                            "Error: Unable to fetch data from OpenAI API.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }




}
