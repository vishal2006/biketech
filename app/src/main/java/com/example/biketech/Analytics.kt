package com.example.biketech


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.data.Mapping
import com.anychart.data.Set
import com.anychart.enums.Orientation
import com.anychart.enums.ScaleStackMode
import com.anychart.scales.Linear

class Analytics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)

        val anyChartView: AnyChartView = findViewById(R.id.any_chart_view)
        anyChartView.setProgressBar(findViewById(R.id.analyticsProgressBar))

        val cartesian: Cartesian = AnyChart.cartesian()


        cartesian.animation(true)

        cartesian.title("Petrol Consumed Per Ride")

        cartesian.yScale().stackMode(ScaleStackMode.VALUE)

        val scalesLinear: Linear = Linear.instantiate()
        scalesLinear.minimum(0.0)
        scalesLinear.maximum(10.0)
        scalesLinear.ticks("{ interval: 1 }")

        val extraYAxis = cartesian.yAxis(1.0)
        extraYAxis.orientation(Orientation.LEFT)
            .scale(scalesLinear)
        extraYAxis.labels()

        val extraXAxis = cartesian.xAxis(0.0)
        extraXAxis.orientation(Orientation.BOTTOM)
        extraXAxis.labels()




        val data: MutableList<DataEntry> = ArrayList()
        data.add(CustomDataEntry("R1", 2096.5, 2040))
        data.add(CustomDataEntry("R2", 2077.1, 1794))
        data.add(CustomDataEntry("R3", 1073.2, 2026))
        data.add(CustomDataEntry("R4", 1061.1, 2341))
        data.add(CustomDataEntry("R5", 2070.0, 1800))
        data.add(CustomDataEntry("R6", 1060.7, 1507))
        data.add(CustomDataEntry("R7", 2062.1, 2701))
        data.add(CustomDataEntry("R8", 1075.1, 1671))
        data.add(CustomDataEntry("R9", 2080.0, 1980))
        data.add(CustomDataEntry("R10", 1054.1, 1041))
        data.add(CustomDataEntry("R11", 1051.3, 813))
        data.add(CustomDataEntry("R12", 1059.1, 691))

        val set: Set = Set.instantiate()
        set.data(data)
        val column2Data: Mapping = set.mapAs("{ x: 'x', value: 'value' }")
        val column1Data: Mapping = set.mapAs("{ x: 'x', value: 'value2' }")

        cartesian.background("FF000000")

        cartesian.column(column1Data)
        cartesian.crosshair(true)

        cartesian.column(column2Data)

        cartesian.xAxis(0).title("Rides")
        cartesian.xAxis(0).ticks()

        cartesian.yAxis(1).title("Petrol Consumed in Litres")

        anyChartView.setChart(cartesian)


    }

    private class CustomDataEntry internal constructor(
        x: String?,
        value: Number?,
        value2: Number?
    ) :
        ValueDataEntry(x, value) {
        init {
            setValue("value2", value2)
        }
    }
}