package com.virtualos.container.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Entity(tableName = "vm_profiles")
data class VmConfigEntity(@PrimaryKey val id:String,val name:String,val romId:String,val versionName:String,val apiLevel:Int,val rootEnabled:Boolean=false,val width:Int=720,val height:Int=1280,val dpi:Int=320,val refreshRate:Int=60,val renderer:String="OpenGL ES",val clipboardSync:Boolean=true,val sharedFolder:Boolean=true,val keepAlive:Boolean=false,val createdAt:Long=System.currentTimeMillis())
@Entity(tableName = "rom_images")
data class RomImageEntity(@PrimaryKey val id:String,val versionName:String,val apiLevel:Int,val architecture:String,val fileSizeBytes:Long,val sha256Checksum:String,val downloadUrl:String,val rootfsLayoutType:String,val localPath:String?=null,val isDownloaded:Boolean=false)
@Dao interface VmDao { @Query("SELECT * FROM vm_profiles ORDER BY createdAt DESC") fun observeAll():Flow<List<VmConfigEntity>>; @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun put(vm:VmConfigEntity); @Delete suspend fun delete(vm:VmConfigEntity) }
@Dao interface RomDao { @Query("SELECT * FROM rom_images ORDER BY apiLevel DESC") fun observeAll():Flow<List<RomImageEntity>>; @Query("SELECT * FROM rom_images WHERE id=:id LIMIT 1") suspend fun get(id:String):RomImageEntity?; @Insert(onConflict=OnConflictStrategy.REPLACE) suspend fun put(rom:RomImageEntity) }
@Database(entities=[VmConfigEntity::class,RomImageEntity::class],version=1,exportSchema=true)
abstract class ContainerDatabase:RoomDatabase(){ abstract fun vmDao():VmDao; abstract fun romDao():RomDao
 companion object { @Volatile private var instance:ContainerDatabase?=null; fun get(context:android.content.Context):ContainerDatabase=instance?: synchronized(this){instance?:Room.databaseBuilder(context.applicationContext,ContainerDatabase::class.java,"virtualos.db").build().also{instance=it}} }
}
