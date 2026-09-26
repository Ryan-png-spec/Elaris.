package it.elaris.app

import android.content.Context
import it.elaris.core.model.WorldState
import it.elaris.core.persistence.SaveCodec
import java.io.File

class LocalSaveRepository(private val context: Context) {
    private val file: File get() = File(context.filesDir, "elaris.save")
    fun exists(): Boolean = file.exists()
    fun save(state: WorldState) {
        val tmp = File(context.filesDir, "elaris.save.tmp")
        tmp.writeText(SaveCodec.encode(state), Charsets.UTF_8)
        if (file.exists()) file.delete()
        check(tmp.renameTo(file)) { "Unable to commit save" }
    }
    fun load(): WorldState = SaveCodec.decode(file.readText(Charsets.UTF_8))
}
