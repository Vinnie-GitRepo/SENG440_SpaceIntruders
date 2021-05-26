package com.example.spaceintruders.gameentities

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
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