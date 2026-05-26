package com.kevinfreyap.jetspending.ui.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.kevinfreyap.jetspending.R
import com.kevinfreyap.jetspending.utils.getFragmentActivity

class BiometricAuthManager(
    private val context: Context
) {
    private val biometricManager = BiometricManager.from(context)

    // Check if device has hardware and fingerprint registered
    fun isBiometricReady(): Boolean {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL

        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt(
        title: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val activity = context.getFragmentActivity()

        if (activity == null) {
            onError("Could not find FragmentActivity")
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object: BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(context.getString(R.string.verify_identity))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}