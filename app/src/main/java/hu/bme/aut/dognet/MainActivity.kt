package hu.bme.aut.dognet

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import hu.bme.aut.dognet.dialog_fragment.ChipReadDialogFragment
import kotlinx.android.synthetic.main.activity_main.*

// TODO fill navigation drawer with use cases
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

    fun setDrawerEnabled(enabled: Boolean) {
        if (enabled) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            actionBarDrawerToggle.isDrawerIndicatorEnabled = true
            actionBarDrawerToggle.syncState()
        }
        else {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            actionBarDrawerToggle.isDrawerIndicatorEnabled = false
            actionBarDrawerToggle.setToolbarNavigationClickListener {
                onBackPressed()
            }
            actionBarDrawerToggle.syncState()
        }
    }

    fun enableForegroundMode() {
        val tagDetected = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val writeTagFilters = arrayOf(tagDetected)
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null)
    }

    fun disableForegroundMode() {
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val fragment = supportFragmentManager.fragments[0].childFragmentManager.fragments[1] as ChipReadDialogFragment

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent!!.action)
            fragment.processNFC(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES))
    }
}
