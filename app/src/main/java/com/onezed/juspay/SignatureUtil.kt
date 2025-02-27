package com.onezed.juspay

import android.content.Context
import android.util.Base64
import android.util.Log
import com.onezed.GlobalVariable.GlobalVariable
import com.onezed.R

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.json.JSONObject
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.security.PrivateKey
import java.security.Signature


object SignatureUtil {
    @JvmStatic
    fun main(args: Array<String>) {
        val data = JSONObject(
            """
            {
                'order_id': 'venkatesh12',
                'first_name': 'Test',
                'last_name': 'Customer',
                'customer_phone': '9876543210',
                'customer_email': 'test@gmail.com',
                'merchant_id': 'udit_juspay',
                'amount': '1.00',
                'customer_id': '9876543210',
                'return_url': 'https://sandbox.juspay.in/end',
                'currency': 'INR',
                'mandate.start_date': '1638535683287',
                'mandate.end_date': '2134731745451',
                'timestamp': '1576227696'
            }
            """.trimIndent()
        )
        val filePath = "private-key.pem"
        //val response = createSignature(data, filePath)
        //println(response)
    }

    fun getSignatures(context: Context): JSONObject {
        val data = JSONObject()
            .put("order_id", "venkatesh12")
//            .put("first_name", "Test")
//            .put("last_name", "Customer")
//            .put("customer_phone", "9876543210")
//            .put("customer_email", "test@gmail.com")
            .put("amount", "1")
            .put("merchant_id", JustPayCred.MERCHANT_ID)
            .put("customer_id", "cth_3XHZBcnJp14cRxqw")
//            .put("return_url", "https://sandbox.juspay.in/end")
//            .put("currency", "INR")
//            .put("mandate.start_date", "1638535683287")
//            .put("mandate.end_date", "2134731745451")
            .put("timestamp", System.currentTimeMillis())

        val filePath = "NOT USING THIS ACTUALLY"
        val response = createSignature(context, data, filePath)
        return response
    }

    private fun createSignature(
        context: Context,
        payload: JSONObject,
        filePath: String
    ): JSONObject {
        return try {
            val privateKey = readPrivateKeyFromFile(context, filePath)
            val privateSignature = Signature.getInstance("SHA256withRSA")
            val requiredFields =
                arrayOf("order_id", "merchant_id", "amount", "timestamp", "customer_id")
            for (key in requiredFields) {
                if (!payload.has(key)) {
                    throw Exception("$key not found in payload")
                }
            }
            val signaturePayload = payload.toString()
            privateSignature.initSign(privateKey)
            privateSignature.update(signaturePayload.toByteArray(StandardCharsets.UTF_8))
            val signature = privateSignature.sign()
            val encodedSignature = Base64.encodeToString(signature, Base64.NO_WRAP)
            val jsonToReturn = JSONObject()
            jsonToReturn.put("signature", encodedSignature)
            jsonToReturn.put("signaturePayload", signaturePayload)
//            jsonToReturn.put(
//                "signaturePayload",
//                Base64.encodeToString(
//                    signaturePayload.toByteArray(StandardCharsets.UTF_8),
//                    Base64.NO_WRAP
//                )
//            )

            jsonToReturn
            /*hashMapOf(
                "signature" to encodedSignature,
                "signaturePayload" to signaturePayload
            )*/
        } catch (e: Exception) {
            println("EXCEPTION AT create signature: $e")
            e.printStackTrace()
            JSONObject()
        }
    }

    @Throws(Exception::class)
    private fun readPrivateKeyFromFile(context: Context, filePath: String): PrivateKey {
        /*if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(BouncyCastleProvider())
        }*/
        context.resources.openRawResource(R.raw.privatekey).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                PEMParser(reader).use { pemParser ->
                    val obj = pemParser.readObject()
                    val converter = JcaPEMKeyConverter()
                    return when (obj) {
                        is PrivateKeyInfo -> converter.getPrivateKey(obj)
                        else -> {
                            Log.e(
                                "TAG",
                                "Unexpected object type in PEM file: ${obj.javaClass.name}"
                            )
                            throw IllegalArgumentException("Unsupported key format in PEM file")
                        }
                    }
                }
            }
        }
        /*context.resources.openRawResource(R.raw.privatekey).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                println("Contents are: ${reader}")
                PEMParser(reader).use { pemParser ->
                    val obj = pemParser.readObject()
                    val converter = JcaPEMKeyConverter()
                    *//*val pemKeyPair = pemParser.readObject() as PEMKeyPair
                    val keyPair: KeyPair = converter.getKeyPair(pemKeyPair)
                    return keyPair.private*//*
                    return when (obj) {
                        is PEMKeyPair -> {
                            val keyPair: KeyPair = converter.getKeyPair(obj)
                            keyPair.private
                        }

                        is PrivateKeyInfo -> {
                            converter.getPrivateKey(obj)
                        }

                        else -> throw IllegalArgumentException("Unsupported key format in PEM file")
                    }
                }
            }
        }*/

    }

    fun readPrivateKey(context: Context): String {
        return context.resources.openRawResource(R.raw.privatekey).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                val stringBuilder = StringBuilder()
                reader.forEachLine { line ->
                    stringBuilder.appendLine(line)
                }
                stringBuilder.toString()
            }
        }
    }
}
