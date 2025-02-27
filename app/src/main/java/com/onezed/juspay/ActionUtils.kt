package com.onezed.juspay

import android.content.Context
import com.onezed.GlobalVariable.GlobalVariable
import com.onezed.Model.UserProfileModel
import com.onezed.juspay.JustPayCred.ACTION_GET_SESSION_TOKEN
import com.onezed.juspay.JustPayCred.ACTION_MANAGEMENT
import com.onezed.juspay.JustPayCred.ACTION_ONBOARDING_N_PAY
import com.onezed.juspay.JustPayCred.MERCHANT_CHANNEL_ID
import com.onezed.juspay.JustPayCred.MERCHANT_ID
import com.onezed.juspay.JustPayCred.MERCHANT_VPA
import com.onezed.juspay.PayloadUtils.getSignedContent
import org.json.JSONObject

object ActionUtils {

    fun getUpiOnboardingNPayBody(context: Context): JSONObject {
        val requestBody = JSONObject()

        val createSigns = getSignedContent(context, createUpiOnboardingSignature())
        requestBody.put("signaturePayload", createSigns["signaturePayload"])
        requestBody.put("action", ACTION_ONBOARDING_N_PAY)
        requestBody.put("customerMobileNumber", UserProfileModel.getInstance().mobileNo)
        requestBody.put("protected", createSigns["protected"])
        requestBody.put("signature", createSigns["signature"])
        //requestBody.put("accountRefId", "")
        requestBody.put("showStatusScreen", true)
        //requestBody.put("bankCodes", listOf(""))
        //requestBody.put("udfParameters", "{}")
        return requestBody
    }
    fun getSessionTokenBody(context: Context): JSONObject{
        val requestBody = JSONObject()
        val sessionTokenSigns = getSignedContent(context, createSessionTokenSignature())
        requestBody.put("signaturePayload",sessionTokenSigns["signaturePayload"])
        requestBody.put("action", ACTION_GET_SESSION_TOKEN)
        requestBody.put("protected",sessionTokenSigns["protected"])
        requestBody.put("signature",sessionTokenSigns["signature"])

        return requestBody
    }
    fun getManagementBody(context: Context):JSONObject{
        val requestBody = JSONObject()
        val sessionTokenSigns = getSignedContent(context, createSessionTokenSignature())
        requestBody.put("action", ACTION_MANAGEMENT)
        requestBody.put("signaturePayload",sessionTokenSigns["signaturePayload"])
        requestBody.put("protected",sessionTokenSigns["protected"])
        requestBody.put("signature",sessionTokenSigns["signature"])
        requestBody.put("shouldExitOnDeregister",true)
        return requestBody
    }
    private fun createSessionTokenSignature(): String{
        val sessionPayload = JSONObject()
        sessionPayload.apply {
            put("merchantCustomerId", generateUniqueRequestId())
            put("timestamp",System.currentTimeMillis().toString())
            put("merchantId", MERCHANT_ID)
            put("merchantChannelId", MERCHANT_CHANNEL_ID)
        }
        return sessionPayload.toString()
    }
    private fun createUpiOnboardingSignature(): String {
        val signaturePayload = JSONObject()
        signaturePayload.put("merchantCustomerId", generateUniqueRequestId())
        signaturePayload.put("merchantId", MERCHANT_ID)
        signaturePayload.put("merchantChannelId", MERCHANT_CHANNEL_ID)
        signaturePayload.put("merchantVpa", MERCHANT_VPA)
        //signaturePayload.put("amount", PAY_AMOUNT)
        signaturePayload.put("amount", GlobalVariable.juspayAmount)
        signaturePayload.put("merchantRequestId", generateUniqueRequestId()) //TODO> DOUBT, ITS NOT A REQ ID/ SO WHERE TO GET THIS FROM
        signaturePayload.put("timestamp", System.currentTimeMillis().toString())
        //signaturePayload.put("bankAccountHashes", "")
        //signaturePayload.put("mutualFundDetails", "")
        /*signaturePayload.apply {
            put("merchantCustomerId", "QAtT64ggckEdUfuWoBicPkldQzdxYOxIE7p")
            put("merchantId", "DIALMYCAUAT")
            put("merchantChannelId", "DIALMYCAUATAPP")
            put("merchantVpa", "dialmycauat@ypay")
            put("amount", "2.05")
            put("merchantRequestId", "jmhtJLXpjNFFv7pe43Jq0zBJH1IQimzLXk8")
            put("timestamp", System.currentTimeMillis().toString())
        }*/
        /*val sign = signaturePayload.toString().toByteArray(charset = Charsets.UTF_8)
        return Base64.getEncoder().encodeToString(sign)*/

        return signaturePayload.toString()
    }

    private fun generateUniqueRequestId(): String{
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvz0123456789"
        val randomPart = (1..35).map { allowedChars.random() }.joinToString("")
        return randomPart
    }

}