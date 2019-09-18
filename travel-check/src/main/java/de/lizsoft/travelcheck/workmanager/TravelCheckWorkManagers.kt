package de.lizsoft.travelcheck.workmanager

import androidx.work.*
import timber.log.Timber
import java.util.concurrent.TimeUnit

object TravelCheckWorkManagers {

    fun runRoutineAutoCheck(repeatInterval: Long, timeUnit: TimeUnit) {
        Timber.d("TravelCheckWorker runRoutineAutoCheck")

        val workRequest1 = PeriodicWorkRequest.Builder(TravelCheckWorker::class.java, repeatInterval, timeUnit)
              //              .addTag(TAG_UNIQUE_WORK_CHAIN_NAME)
              .setConstraints(
                    Constraints.Builder()
                          .setRequiredNetworkType(NetworkType.CONNECTED)
                          //                          .setTriggerContentMaxDelay(10, TimeUnit.SECONDS)
                          //                          .setTriggerContentUpdateDelay(10, TimeUnit.SECONDS)
                          .build()
              )
              .build()

        WorkManager.getInstance()
              .enqueueUniquePeriodicWork(TAG_UNIQUE_WORK_CHAIN_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest1)
    }

    fun isRoutineAutoCheckRunning(): Boolean {
        Timber.d("TravelCheckWorker isRoutineAutoCheckRunning")
        return WorkManager.getInstance()
              .getWorkInfosForUniqueWork(TAG_UNIQUE_WORK_CHAIN_NAME)
              .get().any { it.state != WorkInfo.State.CANCELLED }
    }

    fun cancelRoutineAutoCheckRunning() {
        Timber.d("TravelCheckWorker cancelRoutineAutoCheckRunning")
        WorkManager.getInstance()
              .cancelUniqueWork(TAG_UNIQUE_WORK_CHAIN_NAME)
    }

    private val TAG_UNIQUE_WORK_CHAIN_NAME = "TAG_UNIQUE_WORK_CHAIN_NAME"
}