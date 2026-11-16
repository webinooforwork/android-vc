package com.virtualos.container.importer
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import java.io.File
/** Exports chosen host APK artifacts to private storage; does not install or execute them in a guest. */
class HostAppCloner(private val context:Context){ data class HostApp(val label:String,val packageName:String,val apkPaths:List<String>,val bytes:Long)
 @Suppress("DEPRECATION") fun installedApps():List<HostApp>{val pm=context.packageManager;val apps=if(Build.VERSION.SDK_INT>=33)pm.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))else pm.getInstalledApplications(0);return apps.filter{it.flags and ApplicationInfo.FLAG_SYSTEM==0}.map{a->val paths=buildList{add(a.sourceDir);a.splitSourceDirs?.let(::addAll)};HostApp(pm.getApplicationLabel(a).toString(),a.packageName,paths,paths.sumOf{File(it).length()})}.sortedBy{it.label.lowercase()}}
 fun export(app:HostApp):List<File>{val dir=File(context.filesDir,"apk_exports/${app.packageName}").apply{mkdirs()};return app.apkPaths.mapIndexed{i,p->File(p).copyTo(File(dir,if(i==0)"base.apk" else "split_$i.apk"),overwrite=true)}}
}
