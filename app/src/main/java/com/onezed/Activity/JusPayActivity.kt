package com.onezed.Activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.onezed.GlobalVariable.GlobalVariable
import com.onezed.Model.UserProfileModel
import com.onezed.R
import com.onezed.databinding.ActivityJusPayBinding
import com.onezed.juspay.ActionUtils
import com.onezed.juspay.JustPayCred.ACTION_GET_SESSION_TOKEN
import com.onezed.juspay.JustPayCred.ACTION_INITIATE
import com.onezed.juspay.JustPayCred.ACTION_MANAGEMENT
import com.onezed.juspay.JustPayCred.ACTION_ONBOARDING_N_PAY
import com.onezed.juspay.JustPayCred.ACTION_UPI_CHECK
import com.onezed.juspay.JustPayCred.CLIENT_ID
import com.onezed.juspay.JustPayCred.CUSTOMER_1
import com.onezed.juspay.JustPayCred.ENV_SANDBOX
import com.onezed.juspay.JustPayCred.ISSUING_PSP
import com.onezed.juspay.JustPayCred.MERCHANT_CHANNEL_ID
import com.onezed.juspay.JustPayCred.MERCHANT_ID

import com.onezed.juspay.JustPayCred.SERVICE
import com.onezed.juspay.PayloadUtils.getSignedContent
import `in`.juspay.hyperinteg.HyperServiceHolder
import `in`.juspay.hypersdk.data.JuspayResponseHandler
import `in`.juspay.hypersdk.ui.HyperPaymentsCallbackAdapter
import org.json.JSONObject
import java.util.UUID

class JusPayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJusPayBinding
    private var hyperServicesHolder: HyperServiceHolder? = null
    private var initiatePayload: JSONObject? = null
    private lateinit var signatures: JSONObject
    private val PERMISSION_REQUEST_CODE = 100
    private lateinit var jwsPayload: JSONObject
    var tAmount=0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJusPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get Data from Intent
        val amount = intent.getStringExtra("amount") ?: "0"
        Log.v("JuspayData",amount)
        val numberDouble: Double = amount.toDouble()
        GlobalVariable.juspayAmount=numberDouble;
       //PAY_AMOUNT =amount;

        if (!hasRequiredPermissions()) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.SEND_SMS
                ), PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onStart() {
        super.onStart()
        hyperServicesHolder = HyperServiceHolder(this)
        initiateUpiSDK()
        hyperServicesHolder!!.setCallback(createHyperPaymentsCallbackAdapter())
    }

    override fun onStop() {
        super.onStop()
        Log.v("stop","stop")
        callTerminate()
    }

    private fun callTerminate() {
        hyperServicesHolder?.terminate()
    }

    private fun callProcess(innerPayload: JSONObject, action: String) {
        println("Call Process Initiate")
        val sdkPayload = JSONObject()

        try {
            if (action != ACTION_ONBOARDING_N_PAY) sdkPayload.put("action", ACTION_UPI_CHECK)

            sdkPayload.put("requestId", "" + UUID.randomUUID())
            sdkPayload.put("service", SERVICE)
            sdkPayload.put("payload", innerPayload)
            println("PROCESSING ACTION_$action with request body \n\t====== \n $sdkPayload \n\t======\n")
        } catch (e: Exception) {
            println("Call Process Catch--->$e")
            e.printStackTrace()
        }
        if (hyperServicesHolder!!.isInitialised) hyperServicesHolder?.process(sdkPayload)
    }

    /*
        fun processUIWithSDK(requiredPayload: JSONObject, action: String) {
            if (action == ACTION_UPDATE_AUTH) {
                val formatter = DateTimeFormatter.ISO_INSTANT
                requiredPayload.put("authExpiry", formatter.format(Instant.now()))
            }
            requiredPayload.put("action", action)

            Log.d("TAG", "processUIWithSDK: process for $action with payload $requiredPayload")

            callProcess(requiredPayload)
        }
    */
    fun processUI(requiredPayload: JSONObject, action: String) {
        callProcess(requiredPayload, action);
    }

    private fun createInitiatePayload(): JSONObject {
        val sdkPayload = JSONObject()
        val innerPayload = JSONObject()
        val signaturePayload = JSONObject()

        try {
            println("IN TRY")
            //create signature payload
            signaturePayload.put("merchantId", MERCHANT_ID)
            signaturePayload.put("merchantChannelId", MERCHANT_CHANNEL_ID)
            signaturePayload.put("timestamp", System.currentTimeMillis())
            signaturePayload.put("merchantCustomerId", CUSTOMER_1)
            print(UserProfileModel.getInstance().profileId);
            Log.v("profileId",UserProfileModel.getInstance().profileId.toString());
            jwsPayload =
                getSignedContent(this, signaturePayload.toString())
            /*val jwsSignature = getEncodedSignature(this,signaturePayload.toString())*/


            innerPayload.put("issuingPsp", ISSUING_PSP)
            innerPayload.put("enableJwsAuth", true)
            innerPayload.put("action", ACTION_INITIATE)
            innerPayload.put("clientId", CLIENT_ID)
            innerPayload.put("merchantLoader", true)
            innerPayload.put("protected", jwsPayload["protected"])
            innerPayload.put("signature", jwsPayload["signature"])
            innerPayload.put("signaturePayload", jwsPayload["signaturePayload"])
            innerPayload.put("environment", ENV_SANDBOX)


            sdkPayload.put("requestId", "" + UUID.randomUUID())
            sdkPayload.put("service", SERVICE)
            sdkPayload.put("payload", innerPayload)

        } catch (e: Exception) {
            println("In catch: $e")
            e.printStackTrace()
        }
        return sdkPayload
    }

    private fun createHyperPaymentsCallbackAdapter(): HyperPaymentsCallbackAdapter {
        return object : HyperPaymentsCallbackAdapter() {
            override fun onEvent(
                jsonObject: JSONObject, responseHandler: JuspayResponseHandler?
            ) {
                try {
                    val event = jsonObject.getString("event")
                    Log.i("TAG", "onEvent: event is :: $event")
                    if (event == "hide_loader") {
                        //binding.progressBar.visibility = View.GONE
                    } else if (event == "show_loader") {
                        //binding.progressBar.visibility = View.VISIBLE
                    } else if (event == "initiate_result") {
                        val innerPayload = jsonObject.optJSONObject("payload")
                        println("Initiate Result Payload is-->$innerPayload")
                    } else if (event == "process_result") {
                        val innerPayload = jsonObject.optJSONObject("payload")
                        println("PROCESS RESULT PAYLOAD WAS : $innerPayload and raw was $jsonObject")
                        //startActivity(HomeActivity)
//                        if (innerPayload.getString("status").equals("BACKPRESS")){
//                            val intent = Intent(this@JusPayActivity, HomeActivity::class.java)
//                            startActivity(intent)
//                        }



                    } else if (event == "log_stream") {
                        val innerPayload = jsonObject.optJSONObject("payload")
                        println("event_log--->$innerPayload")
                    } else if (event == "session_expired") {
                        val innerPayload = jsonObject.optJSONObject("payload")
                        Log.d("TAG", "onEvent: SESSION EXPIRED $innerPayload")

                    } else if (event == "session_metadata") {
                        val innerPayload = jsonObject.optJSONObject("payload")
                        println("Metadata Result Payload is-->$innerPayload")


                        jwsPayload = JSONObject()
                        val signaturePayload = JSONObject()
                        signaturePayload.put("merchantId", MERCHANT_ID)
                        signaturePayload.put("merchantChannelId", MERCHANT_CHANNEL_ID)
                        signaturePayload.put("timestamp", System.currentTimeMillis())
                        signaturePayload.put("merchantCustomerId", CUSTOMER_1)

                        jwsPayload =
                            getSignedContent(this@JusPayActivity, signaturePayload.toString())

                        val newSessionPayload = ActionUtils.getSessionTokenBody(
                            context = this@JusPayActivity
                        )
                        processUI(newSessionPayload, ACTION_GET_SESSION_TOKEN)


                        val newPayload = ActionUtils.getUpiOnboardingNPayBody(
                            context = this@JusPayActivity
                        )
                        processUI(newPayload, ACTION_ONBOARDING_N_PAY)

                        val managementPayload = ActionUtils.getManagementBody(
                            context = this@JusPayActivity
                        )
                        processUI(managementPayload, ACTION_MANAGEMENT)

                    }

                } catch (e: Exception) {
                    Log.e(
                        "SOME_TAG",
                        "onEvent: Exception occurred at HyperPaymentsCallbackAdapter -> $e ",
                    )
                }
            }
        }
    }

    private fun initiateUpiSDK() {
        if (!hyperServicesHolder!!.isInitialised) {
            initiatePayload = createInitiatePayload()
            Log.i("TAG", "initiatePaymentsSDK: payload was=> $initiatePayload")
            hyperServicesHolder!!.initiate(initiatePayload)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {
        val handleBackPress: Boolean = hyperServicesHolder?.onBackPressed() == true
        if (handleBackPress) {
            super.onBackPressed()
        }
        startActivity(Intent(this,HomeActivity::class.java))
        finish()
    }

    private fun hasRequiredPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.INTERNET
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_NETWORK_STATE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // All permissions granted
                hyperServicesHolder?.hyperServices?.onRequestPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults
                )
            } else {
                Toast.makeText(this, "Permissions are required to proceed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            hyperServicesHolder!!.hyperServices.onActivityResult(requestCode, resultCode, data);
        }
    }
}



