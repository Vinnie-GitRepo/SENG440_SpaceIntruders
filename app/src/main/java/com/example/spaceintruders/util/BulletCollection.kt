package com.example.spaceintruders.util

import com.example.spaceintruders.gameentities.Bullet
import java.util.concurrent.locks.ReentrantLock

/**
 * This class makes sure the creation of bullets is thread safe
 */
class BulletCollection {
    private val lock = ReentrantLock()
    private val bullets: MutableSet<Bullet> = mutableSetOf()

    fun getBulletCopy() : Set<Bullet> {
        try {
            lock.lock()
            val set = mutableSetOf<Bullet>()
            set.addAll(bullets)
            return set
        } finally {
            lock.unlock()
        }
    }

    fun deleteBullet(bullet: Bullet) {
        try {
            lock.lock()
            bullets.remove(bullet)
        } finally {
            lock.unlock()
        }
    }

    fun addBullet(bullet: Bullet) {
        try {
            lock.lock()
            bullets.add(bullet)
        } finally {
            lock.unlock()
        }
    }
}