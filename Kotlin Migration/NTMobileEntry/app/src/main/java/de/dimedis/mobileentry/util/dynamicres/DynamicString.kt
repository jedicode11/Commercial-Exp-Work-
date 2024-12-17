package de.dimedis.mobileentry.util.dynamicres
/**
 * Created by Softeq Development Corporation
 * http://www.softeq.com
 */
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.google.gson.annotations.SerializedName
import de.dimedis.mobileentry.BuildConfig
import de.dimedis.mobileentry.ConfigPref
import de.dimedis.mobileentry.backend.response.Localize
import de.dimedis.mobileentry.util.ConfigPrefHelper.getLanguages
import java.lang.reflect.Field
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.Map

class DynamicString internal constructor(var mContext: Context, var pref: ConfigPref) {
    private var _localization: Map<String?, Localize?>? = null
    var resStrings: HashMap<String?, String?> = HashMap()
    var resIds: HashMap<Int?, String?> = HashMap()

    init {
        val lang = getLanguages()
        if (lang != null && lang.isNotEmpty()) {
            init(lang)
            setLang(pref.currentLanguage)
        }
    }

    fun init(localization: Map<String?, Localize?>?) {
        _localization = localization
    }

    fun setLang(lang: String?) {
        if (TextUtils.isEmpty(lang)) return
        reflectionScan(_localization!![lang])
    }

    fun reflectionScan(`object`: Localize?) {
        val clazz: Class<*> = `object`!!.javaClass
        val fields = getAllFields(clazz)
        for (field in fields) {
            if (field.isAnnotationPresent(SerializedName::class.java) && field.isAnnotationPresent(SerializedResId::class.java)) {
                val seName = field.getAnnotation(SerializedName::class.java)
                val resID = field.getAnnotation(SerializedResId::class.java)
                try {
                    val isAccess = field.isAccessible
                    field.isAccessible = true
                    var objField = field[`object`]
                    if (objField == null) {
                        Log.w(TAG,"filed is null for: " + seName!!.value + " setting default value: " + mContext.getString(resID!!.value)
                        )
                        objField = mContext.getString(resID.value)
                    }
                    Log.w(TAG, ">>|" + seName!!.value + "|=|" + objField.toString() + "|")
                    resStrings[seName.value] = objField.toString()
                    resIds[resID!!.value] = objField.toString()
                    field.isAccessible = isAccess
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getString(resId: Int): String? {
        return if (resIds.containsKey(resId)) {
            resIds[resId]
        } else mContext.getString(resId)
    }

    companion object {
        private const val TAG = "DynamicString"
        var instance: DynamicString = null!!

        fun getAllFields(type: Class<*>): List<Field> {
            val result: MutableList<Field> = LinkedList(listOf(*type.declaredFields))
            if (type.superclass != null) {
                result.addAll(getAllFields(type.superclass))
            }
            return result
        }

        fun init(mContext: Context, pref: ConfigPref) {
            if (instance == null) instance = DynamicString(mContext, pref)
        }

        fun scan(view: View?) {
            if (view == null) return
            val gView = view as ViewGroup
            val count = gView.childCount
            var tView: View?
            var objectTag: Any?
            for (i in 0 until count) {
                tView = gView.getChildAt(i)
                if (tView is ViewGroup) {
                    scan(tView)
                }
                if (tView is TextView) {
                    val isEditText = tView is EditText
                    objectTag = tView.tag
                    Log.w("DynamicString", "TAG:$objectTag")
                    if (objectTag is String) {
                        val tv = tView
                        if (instance.resStrings.containsKey(objectTag)) {
                            if (!isEditText) {
                                tv.text = instance.resStrings[objectTag]
                            } else {
                                tv.hint = instance.resStrings[objectTag]
                            }
                        } else {
                            if (BuildConfig.DEBUG) {
                                Log.w("DynamicString", "*!!TAG:" + objectTag + " tx:" + tv.text + "| lM:" + instance.resStrings.size)
                                tv.text = tv.text
                                for ((key, value) in instance.resStrings) {
                                    Log.w("DynamicString", "MAP $key v:$value")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}