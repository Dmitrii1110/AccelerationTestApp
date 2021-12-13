package com.project.accelerationtestapp

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    //1.Сразу обращаемся к Сенсор менеджеру
    lateinit var sManager : SensorManager
    //12.>>Второй урок<<. Создаём массивы
    private var magnetic = FloatArray(9) //первый массив
    private var gravity = FloatArray(9) //второй массив

    private var accrs = FloatArray(3) //третий массив (он будет записывать данные с акселерометра)
    private var magf = FloatArray(3) //четвертый массив (он будет записывать данные с магнетик филд)
    private var values = FloatArray(3) //пятый массив (он будет записывать уже готовые данные)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //>>>Убираем статус бар!!!!!
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        title = "Acceleration"

        setContentView(R.layout.activity_main)
        //6. Не будем использовать binding так как мало кода а просто через findViewById
        val tvSensor = findViewById<TextView>(R.id.tvSensor)
        //22.находим элемент в разметке линеар лояут (уровень)
        val lRotation = findViewById<LinearLayout>(R.id.lRotation)
        //2. Ниже инициализируем эту функцию. Из ГетСистемСервиса берём константу,
        // которая укажет на Сенсор Сервис и показываем (as) что мы тем самым подключились к СенсорМенаджер
        sManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //3.Теперь нам надо получить сам сенсор (какой именно будем использовать)
        val sensor = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        //13. Создаём второй сенсор магнетик филд
        val sensor2 = sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        //4.Теперь в Сенсор менеджер нужно зарегистрировать слушатель, который будет получать данные
        //с нашего сенсора, загистрируем его в системе для получения данных
        val sListener = object : SensorEventListener{
            //5.1 в Этот метод будут приходить данные
            override fun onSensorChanged(event: SensorEvent?) {
                //15. Удалил данные прошлые и делаем проверку уже с учетом акселерометра и магнетика
                when(event?.sensor?.type){
                    Sensor.TYPE_ACCELEROMETER -> accrs = event.values.clone()//16.передаем данные из акселерометра в массив и показываем итоговое значение
                    Sensor.TYPE_MAGNETIC_FIELD -> magf = event.values.clone()//17.передаем данные из magnetic field в массив и показываем итоговое значение

                }

                //18.Объединяем значения от акселератора и магнетика
                SensorManager.getRotationMatrix(gravity, magnetic, accrs, magf)
                //19.Создаём временный массив чтобы получить данные remap (данные в координатной системе)
                val outGravity = FloatArray(9)
                SensorManager.remapCoordinateSystem(gravity,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    outGravity //20.Сэда мы передали значения
                )

                //21.Переводим значения в градусы
                SensorManager.getOrientation(outGravity, values)
                val degree = values[2] * 57.2958f //22.Создаем константу для перевода в градусы
                val rotate = 270 + degree //24.чтобы выровнить относительно оси х (подстройка под альбомную ориентацию смартфона)
                lRotation.rotation = rotate //23.
                val rData = 90 + degree//26.Чтобы значения показывали ноль при выравнивании по оси х
                //26.Ниже Проверка если совпадает уровень то цвет зеленный, если нет то красный
                val color = if(rData.toInt() == 0){
                    Color.GREEN
                } else {
                    Color.RED
                }
                lRotation.setBackgroundColor(color)//27.Передаём цвет в наш бекграунд колер линеар лояут
                tvSensor.text = rData.toInt().toString() //25.ИТОГОВЫЙ РЕЗУЛЬТАТ ВЫЧЕСЛЕНИЙ

            }

            //5.2 Данный метод будет указывать точность измерения когда занчения будут меняться
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

        }
        //7. Подготавливаем данные, чтобы использовать Сенсор Менеджер
        sManager.registerListener(sListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        //14. Подготавливаем данные, чтобы использовть магнетик филд
        sManager.registerListener(sListener, sensor2, SensorManager.SENSOR_DELAY_NORMAL)
    }
}