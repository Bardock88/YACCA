@file:OptIn(ExperimentalForeignApi::class)

package com.evandhardspace.yacca.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.StableRef
import kotlinx.cinterop.alignOf
import kotlinx.coroutines.flow.Flow
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.value
import platform.Foundation.NSLog
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.SystemConfiguration.SCNetworkReachabilityCallBack
import platform.SystemConfiguration.SCNetworkReachabilityContext
import platform.SystemConfiguration.SCNetworkReachabilityCreateWithAddress
import platform.SystemConfiguration.SCNetworkReachabilityFlags
import platform.SystemConfiguration.SCNetworkReachabilityFlagsVar
import platform.SystemConfiguration.SCNetworkReachabilityGetFlags
import platform.SystemConfiguration.SCNetworkReachabilityRef
import platform.SystemConfiguration.SCNetworkReachabilitySetCallback
import platform.SystemConfiguration.SCNetworkReachabilitySetDispatchQueue
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsConnectionRequired
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsReachable
import platform.darwin.dispatch_queue_attr_make_with_qos_class
import platform.darwin.dispatch_queue_create
import platform.posix.sockaddr_in
import kotlinx.coroutines.flow.MutableStateFlow
import platform.posix.sockaddr
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.convert
import platform.SystemConfiguration.*
import platform.posix.*

// todo not working properly
@OptIn(ExperimentalForeignApi::class)
internal fun createNetworkMonitor(): NetworkMonitor {
    val stableRef = "some stable ref"
    val reachabilityUtil: ReachabilityUtil = ReachabilityUtilImpl()

    val sizeSockaddr = sizeOf<sockaddr_in>()
    val alignSockaddr = alignOf<sockaddr_in>()
    val zeroAddress =
        nativeHeap.alloc(sizeSockaddr, alignSockaddr).reinterpret<sockaddr_in>().apply {
            sin_len = sizeOf<sockaddr_in>().toUByte()
            sin_family = AF_INET.convert()

        }

    val reachabilityRef: SCNetworkReachabilityRef =
        SCNetworkReachabilityCreateWithAddress(null, zeroAddress.ptr.reinterpret<sockaddr>())
            ?: throw IllegalStateException("Failed on SCNetworkReachabilityCreateWithAddress")

    val networkMonitorImpl = object : NetworkMonitor {
        val isConnectedFlow = MutableStateFlow(reachabilityRef.isConnected(reachabilityUtil))
        override val isConnected: Flow<Boolean> = isConnectedFlow
    }

    val dispatchQueueAttr = dispatch_queue_attr_make_with_qos_class(null, QOS_CLASS_DEFAULT, 0)

    val reachabilitySerialQueue =
        dispatch_queue_create("com.plusmobileapps.konnectivity", dispatchQueueAttr)

    NSNotificationCenter.defaultCenter.addObserverForName(
        name = "ReachabilityChangedNotification",
        `object` = null,
        queue = NSOperationQueue.mainQueue,
        usingBlock = {
            networkMonitorImpl.isConnectedFlow.tryEmit(
                reachabilityRef.isConnected(reachabilityUtil)
            )
        }
    )

    val selfPtr = StableRef.create(stableRef)

    val sizeSCNetReachCxt = sizeOf<SCNetworkReachabilityContext>()
    val alignSCNetReachCxt = alignOf<SCNetworkReachabilityContext>()
    val context = nativeHeap.alloc(sizeSCNetReachCxt, alignSCNetReachCxt)
        .reinterpret<SCNetworkReachabilityContext>().apply {
            version = 0
            info = selfPtr.asCPointer()
            retain = null
            release = null
            copyDescription = null
        }

    val callback: SCNetworkReachabilityCallBack =
        staticCFunction { _: SCNetworkReachabilityRef?, _: SCNetworkReachabilityFlags, info: COpaquePointer? ->
            if (info == null) {
                return@staticCFunction
            }
            try {
                NSNotificationCenter.defaultCenter.postNotificationName(
                    "ReachabilityChangedNotification",
                    null
                )
            } catch (error: Throwable) {
                NSLog("SCNetworkReachabilityCallBack error: ${error.message}")
            }
        }

    if (!SCNetworkReachabilitySetCallback(reachabilityRef, callback, context.ptr)) {
        throw IllegalStateException("Failed on SCNetworkReachabilitySetCallback")
    }
    if (!SCNetworkReachabilitySetDispatchQueue(reachabilityRef, reachabilitySerialQueue)) {
        throw IllegalStateException("Failed on SCNetworkReachabilitySetDispatchQueue")
    }

    return networkMonitorImpl
}


private fun SCNetworkReachabilityRef.isConnected(util: ReachabilityUtil): Boolean {
    kSCNetworkReachabilityFlagsReachable
    val flags = getReachabilityFlags(util)
    val isReachable = flags.contains(kSCNetworkReachabilityFlagsReachable)
    val needsConnection = flags.contains(kSCNetworkReachabilityFlagsConnectionRequired)
    return isReachable && !needsConnection
}

private fun SCNetworkReachabilityRef.getReachabilityFlags(util: ReachabilityUtil): Array<SCNetworkReachabilityFlags> {
    val flags = util.getReachabilityFlags(this) ?: return emptyArray()

    val result = arrayOf(
        kSCNetworkReachabilityFlagsTransientConnection,
        kSCNetworkReachabilityFlagsReachable,
        kSCNetworkReachabilityFlagsConnectionRequired,
        kSCNetworkReachabilityFlagsConnectionOnTraffic,
        kSCNetworkReachabilityFlagsInterventionRequired,
        kSCNetworkReachabilityFlagsConnectionOnDemand,
        kSCNetworkReachabilityFlagsIsLocalAddress,
        kSCNetworkReachabilityFlagsIsDirect,
        kSCNetworkReachabilityFlagsIsWWAN,
        kSCNetworkReachabilityFlagsConnectionAutomatic
    ).filter {
        (flags and it) > 0u
    }
        .toTypedArray()
    NSLog("Konnectivity: SCNetworkReachabilityFlags: ${result.contentDeepToString()}")
    return result
}

private interface ReachabilityUtil {
    fun getReachabilityFlags(
        reachabilityRef: SCNetworkReachabilityRef
    ): SCNetworkReachabilityFlags?
}

@OptIn(ExperimentalForeignApi::class)
private class ReachabilityUtilImpl : ReachabilityUtil {

    override fun getReachabilityFlags(
        reachabilityRef: SCNetworkReachabilityRef
    ): SCNetworkReachabilityFlags? = memScoped {
        val flags = alloc<SCNetworkReachabilityFlagsVar>()
        return (if (SCNetworkReachabilityGetFlags(reachabilityRef, flags.ptr)) flags.value else null).also {
            NSLog("ReachabilityUtil getFlags - $it")
        }
    }
}
