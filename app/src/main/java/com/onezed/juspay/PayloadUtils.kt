package com.onezed.juspay


import android.content.Context
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.RSASSASigner
import org.json.JSONObject
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PrivateKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

object PayloadUtils {
    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class)
    fun getSignedContent(context: Context, payload: String): JSONObject {
        val contentPayload = Payload(payload)
        val privateKeyPkcs8 = SignatureUtil.readPrivateKey(context) // Private Key
        val privateKey = getEncodedPrivateKey(privateKeyPkcs8)
        return try {
            val rsa = RSASSASigner(privateKey)
            val alg = JWSAlgorithm.RS256
            val header = JWSHeader.Builder(alg)
                .keyID(JustPayCred.KID) // KID, This will be shared by Juspay
                .build()
            val jws = JWSObject(header, contentPayload)
            jws.sign(rsa)
            createPayload(jws.serialize())
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class)
    fun getEncodedSignature(context: Context, payload: String): String {
        val contentPayload = Payload(payload)
        val privateKeyPkcs8 = SignatureUtil.readPrivateKey(context) // Private Key
        val privateKey = getEncodedPrivateKey(privateKeyPkcs8)
        return try {
            val rsa = RSASSASigner(privateKey)
            val alg = JWSAlgorithm.RS256
            val header = JWSHeader.Builder(alg)
                .keyID(JustPayCred.KID) // KID, This will be shared by Juspay
                .build()
            val jws = JWSObject(header, contentPayload)
            jws.sign(rsa)
            createNewPayload(jws.serialize())
            //signature
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class)
    private fun getEncodedPrivateKey(pspPrivateKey: String): PrivateKey {
        val key = pspPrivateKey.replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s+".toRegex(), "")
        val pkcs8EncodedBytes = Base64.getDecoder().decode(key)
        val keySpec = PKCS8EncodedKeySpec(pkcs8EncodedBytes)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePrivate(keySpec)
    }

    @Throws(InvalidKeySpecException::class)
    fun createPayload(payloadData: String): JSONObject {
        val payloadKeys = arrayOf("protected", "signaturePayload", "signature")
        val payloadContent = payloadData.split(".").toTypedArray()
        val payload = JSONObject()
        if (payloadContent.size == 3) {
            for (i in payloadKeys.indices) {
                payload.put(payloadKeys[i], payloadContent[i])
            }
        } else {
            payload.put("error", "Payload string is not in correct format")
        }
        print("Generated payload => $payload")
        return payload
    }
    @Throws(InvalidKeySpecException::class)
    fun createNewPayload(payloadData: String): String {
        /*val payloadKeys = arrayOf("protected", "signaturePayload", "signature")
        println("like-->$payloadData")
        val payloadContent = payloadData.split(".").toTypedArray()
        val payload = JSONObject()
        if (payloadContent.size == 3) {
            for (i in payloadKeys.indices) {
                payload.put(payloadKeys[i], payloadContent[i])
            }
        } else {
            payload.put("error", "Payload string is not in correct format")
        }
        print("Generated payload => $payload")*/
        val payload = JSONObject()
        println("payload_new-->$payloadData")
        return payloadData
    }
}