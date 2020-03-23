package hu.bme.aut.dognet

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.ndeftools.Message
import org.ndeftools.externaltype.AndroidApplicationRecord


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var nfcPendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawer_layout)

        setSupportActionBar(my_toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, my_toolbar, R.string.nav_drawer_open, R.string.nav_drawer_closed)
        drawer_layout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        nav_view.setupWithNavController(navController)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcPendingIntent = PendingIntent.getActivity(this, 0, Intent(this, this.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    private fun enableForegroundMode() {
        val tagDetected = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val writeTagFilters = arrayOf(tagDetected)
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    }

    private fun disableForegroundMode() {
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent!!.action) {
           Log.d("Main", "Hello NFC!")

            val messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            if (messages != null) {
                Log.d("Main", "Found " + messages.size + " NDEF messages")

                vibrate()

                for (m in messages) {
                    val records = Message(m as NdefMessage)

                    for (r in records) {
                        if (r is AndroidApplicationRecord) {
                            Log.d("Main", "Package is: " + r.domain + " " + r.type)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableForegroundMode()
    }

    override fun onPause() {
        super.onPause()
        disableForegroundMode()
    }

    private fun vibrate() {
        val vibe: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibe.vibrate(500)
    }
}
