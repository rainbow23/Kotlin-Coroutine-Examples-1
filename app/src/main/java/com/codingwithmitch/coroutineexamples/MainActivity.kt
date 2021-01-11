package com.codingwithmitch.coroutineexamples

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            setNewText("Click!")

            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }

    }

    private fun setNewText(input: String){
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }
    private suspend fun setTextOnMainThread(input: String) {
        withContext (Main) {
            setNewText(input)
        }
    }

    /**
     * Job1 and Job2 run in parallel as different coroutines
	 * Also see "Deferred, Async, Await" branch for parallel execution
     */
    private suspend fun fakeApiRequest() {
        withContext(IO) {
            println("debug: start")
            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name}")
                    var result1 = getResult1FromApi()
                    setTextOnMainThread("job1: Got $result1")

                    var result2 = getResult2FromApi()
                    setTextOnMainThread("job1: Got $result2")

                    result1 = getResult1FromApi()
                    setTextOnMainThread("job1: Got $result1")

                    result2 = getResult2FromApi()
                    setTextOnMainThread("job1: Got $result2")
                }
                println("debug: compeleted job1 in $time1 ms.")
            }

            val job2 = launch {
                val time2 = measureTimeMillis {
                    println("debug: launching job2 in thread: ${Thread.currentThread().name}")
                    val result2 = getResult2FromApi()
                    setTextOnMainThread("job2: Got $result2")
                }
                println("debug: compeleted job2 in $time2 ms.")
            }

//            var result = ""
//            val job = launch {
//                println("debug: job is started")
//                result = getResult1FromApi()
//            }.join()
//            println("debug: result = $result")


            println("debug: end")
        }
    }

    private suspend fun getResult1FromApi(): String {
        delay(2000)
        return "Result #1"
    }

    private suspend fun getResult2FromApi(): String {
        delay(1500)
        return "Result #2"
    }

}