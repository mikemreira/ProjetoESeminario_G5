package isel.pt.ps.projeto.casbin
/*
import org.casbin.jcasbin.model.Model
import org.casbin.jcasbin.persist.Adapter
import org.casbin.jcasbin.persist.Helper
import org.casbin.jcasbin.persist.Helper.loadPolicyLineHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class IsAdapter(private val classPath: String) : Adapter {
    override fun loadPolicy(model: Model) {
        if (classPath == "") {
            // throw new Error("invalid file path, file path cannot be empty");
            return
        }

        loadPolicyClassPath(model) { line: String?, model: Model? -> Helper.loadPolicyLine(line, model) }
    }

    override fun savePolicy(model: Model) {
        LOG.warn("savePolicy is not implemented !")
    }

    override fun addPolicy(sec: String, ptype: String, rule: List<String>) {
        throw Error("not implemented")
    }

    override fun removePolicy(sec: String, ptype: String, rule: List<String>) {
        throw Error("not implemented")
    }

    override fun removeFilteredPolicy(sec: String, ptype: String, fieldIndex: Int, vararg fieldValues: String) {
        throw Error("not implemented")
    }

    private fun loadPolicyClassPath(model: Model, handler: loadPolicyLineHandler<String, Model>) {
        val isStream = IsAdapter::class.java.getResourceAsStream(classPath)
        val br = BufferedReader(InputStreamReader(isStream))

        var line: String
        try {
            while ((br.readLine().also { line = it }) != null) {
                handler.accept(line, model)
            }

            isStream.close()
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
            throw Error("IO error occurred")
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(IsAdapter::class.java)
    }
}

 */