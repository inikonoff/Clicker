package com.inikonoff.clicker

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.content.Intent

class WindowTileService : TileService() {
    override fun onClick() {
        val intent = Intent(this, WindowActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivityAndCollapse(intent)
    }

    override fun onStartListening() {
        qsTile?.state = Tile.STATE_INACTIVE
        qsTile?.label = "Кликер окно"
        qsTile?.updateTile()
    }
}
