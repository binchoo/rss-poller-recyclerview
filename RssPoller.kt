package com.corndog.rssfeederrecyclerview.rssfeeder

import rsspoller.RssReference

class RssPoller(val rssReference: RssReference, var interval: Long) {

    private var pollingThread: PollingThread? = null
    private var callback: Callback? = null

    fun start() {
        if (!isPolling()) {
            pollingThread = PollingThread()
            pollingThread?.start()
        }
    }

    fun stop() {
        pollingThread?.interrupt()
        pollingThread = null
    }

    fun addCallback(callback: Callback) {
        this.callback = callback
    }

    fun changeInterval(interval: Long) {
        this.interval = interval
    }

    fun isPolling(): Boolean {
        return pollingThread?.isPolling() ?: false
    }

    interface Callback {
        fun onEachPolling(updatedRssReference: RssReference)
    }

    inner class PollingThread: Thread() {
        var mIsPolling = false

        override fun run() {
            if (!mIsPolling) {
                mIsPolling = true
                while (mIsPolling) {
                    try {
                        rssReference.elems()
                        callback?.onEachPolling(rssReference)
                        sleep(interval)
                    } catch (e: InterruptedException) {
                        mIsPolling = false
                    }
                }
            }
        }

        fun isPolling() = mIsPolling
    }
}