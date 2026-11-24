package com.kevinfreyap.jetspending.ui.biometric

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
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
    private val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

    fun checkAuthAvailability(): BiometricCheckResult {
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricCheckResult.Ready
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricCheckResult.NotEnrolled
            else -> BiometricCheckResult.Unavailable
        }
    }

    fun showBiometricPrompt(
        title: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        subtitle: String? = null,
        onNotEnrolled: () -> Unit = {},
        onCancel: () -> Unit = {}
    ) {
        val activity = context.getFragmentActivity()
        if (activity == null) {
            onError("Could not find FragmentActivity")
            return
        }

        when (checkAuthAvailability()) {
            BiometricCheckResult.NotEnrolled -> {
                onNotEnrolled()
                return
            }
            BiometricCheckResult.Unavailable -> {
                onError(activity.getString(R.string.label_biometric_unavailable))
                return
            }
            BiometricCheckResult.Ready -> Unit
        }
        val resolvedSubtitle = subtitle ?: activity.getString(R.string.label_verify_identity)

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object: BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.d("AppLock", "Biometric succeeded")
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> onCancel()

                    BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL,
                    BiometricPrompt.ERROR_NO_BIOMETRICS -> onNotEnrolled()

                    else -> onError(errString.toString())
                }
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(resolvedSubtitle)
            .setAllowedAuthenticators(
                authenticators
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    fun createEnrollIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    authenticators
                )
            }
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
    }
}