/*
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.common.executors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import android.os.Handler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class HandlerExecutorServiceImplTest {
    private AtomicInteger mCounter = new AtomicInteger();
    Runnable mIncrementCounterRunnable = new Runnable() {
        @Override
        public void run() {
            mCounter.incrementAndGet();
        }
    };
    private HandlerExecutorServiceImpl mExecutorService;

    @Before
    public void setup() {
        Handler handler = new Handler();
        mExecutorService = new HandlerExecutorServiceImpl(handler);
    }

    @Test
    public void testSimpleExecute() {
        ShadowLooper.pauseMainLooper();
        mExecutorService.execute(mIncrementCounterRunnable);
        Assert.assertEquals(0, mCounter.get());
        ShadowLooper.unPauseMainLooper();
        Assert.assertEquals(1, mCounter.get());
    }

    @Test
    public void testDelay() {
        mExecutorService.schedule(mIncrementCounterRunnable, 30, TimeUnit.SECONDS);
        Assert.assertEquals(0, mCounter.get());
        Shadows.shadowOf(ShadowLooper.getMainLooper()).getScheduler().advanceBy(30 * 1000);
        Assert.assertEquals(1, mCounter.get());
    }
}
