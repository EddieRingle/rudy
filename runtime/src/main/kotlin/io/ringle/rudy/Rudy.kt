package io.ringle.rudy

import kotlin.platform.platformStatic

public object Rudy {

    platformStatic val sKeyPrefix = "__rudy_"

    platformStatic val sKeyAttached = "${sKeyPrefix}attached"

    platformStatic val sKeyFinished = "${sKeyPrefix}finished"

    platformStatic val sKeyNeedsResume = "${sKeyPrefix}needsResume"

    platformStatic val sKeyResumed = "${sKeyPrefix}resumed"

    platformStatic val sKeyKeyList = "${sKeyPrefix}keyList"

    platformStatic val sKeyNewState = "${sKeyPrefix}newState"

    platformStatic val sKeyState = { key: Int -> "${sKeyPrefix}state[${key}]" }
}
