package ui.anwesome.com.itowview

/**
 * Created by anweshmishra on 01/05/18.
 */

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.MotionEvent
import android.graphics.*

class IToWView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State (var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0) {
        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator (var view : View, var animated : Boolean = false) {

        fun animate(updatecb : () -> Unit) {
            if (animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch (ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class IToW (var i : Int, val state : State = State()) {

        fun draw(canvas : Canvas, paint : Paint) {
            val deg : Float = 15f
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val size : Float = Math.min(w, h)/2
            val updatedSize : Float = (size/2) * state.scales[0]
            val kx : Float = (size) * Math.sin(deg * Math.PI/180).toFloat()
            paint.strokeWidth = size/15
            paint.strokeCap = Paint.Cap.ROUND
            paint.color = Color.parseColor("#311B92")
            canvas.save()
            canvas.translate(w/2, h/2 + size/2)
            for (i in 0..1) {
                for (j in 0..1) {
                    canvas.save()
                    canvas.translate(kx * (1 - 2 * i) * state.scales[1], 0f)
                    canvas.rotate(deg * (1 - 2 * j) * state.scales[2])
                    canvas.save()
                    canvas.translate(0f, -size/2)
                    canvas.drawLine(0f, -updatedSize, 0f, updatedSize, paint)
                    canvas.restore()
                    canvas.restore()
                }
            }
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }
    }

    data class Renderer(var view : IToWView) {

        private val animator : Animator = Animator(view)

        private val iToW : IToW = IToW(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            iToW.draw(canvas, paint)
            animator.animate {
                iToW.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            iToW.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : IToWView  {
            val view : IToWView = IToWView(activity)
            activity.setContentView(view)
            return view
        }
    }
}