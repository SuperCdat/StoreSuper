package com.supercdat.storesuper.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supercdat.storesuper.R
import com.supercdat.storesuper.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var appIconList: Array<String>
    lateinit var applicationNameList: Array<String>
    lateinit var publisherNamelist: Array<String>
    lateinit var timesDownloadedList: Array<String>
    lateinit var downloadLinkList: Array<String>
    lateinit var packageNameList: Array<String>
    lateinit var appDescriptionList: Array<String>
    lateinit var packageSizeList: Array<String>
    lateinit var applicationCategoryList: Array<String>
    lateinit var applicationVersionList: Array<String>



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        CoroutineScope(Dispatchers.IO).launch {
            val url: String =
                "https://gist.githubusercontent.com/SuperCdat/46a1bbdd288ca3fa6f4b4c9332278972/raw/StoreSuper%2520Database.txt"
            val text = getTextFromUrl(url).replace("\n", "")
            withContext(Dispatchers.Main) {
                val globalArray: Array<Array<String>> =
                    arrayListOfListsToArrayOfArrays(stringToArrayListOfListsOfStrings(text))
//                    Toast.makeText(requireActivity(), globalArray.toString(), Toast.LENGTH_LONG)
//                        .show()
                appIconList = globalArray[0]
                applicationNameList = globalArray[1]
                publisherNamelist = globalArray[2]
                timesDownloadedList = globalArray[3]
                downloadLinkList = globalArray[4]
                packageNameList = globalArray[5]
                appDescriptionList = globalArray[6]
                packageSizeList = globalArray[7]
                applicationCategoryList = globalArray[8]
                applicationVersionList = globalArray[9]

                dataList = ArrayList<DataClass>()
                getData()



            }
        }

        recyclerView = root.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getData() {
        for (i in appIconList.indices) {
            val dataClass = DataClass(appIconList[i],
                applicationNameList[i],
                publisherNamelist[i],
                timesDownloadedList[i],
                downloadLinkList[i],
                packageNameList[i],
                appDescriptionList[i],
                packageSizeList[i],
                applicationCategoryList[i],
                applicationVersionList[i]
            )
            dataList.add(dataClass)
        }

        dataList.shuffle()

        recyclerView.adapter = AdapterClass(dataList)
    }

    private fun stringToArrayListOfListsOfStrings(s: String): ArrayList<ArrayList<String>> {
        val outerList = ArrayList<ArrayList<String>>()

        // Remove the outer brackets and split into individual inner lists
        val innerListStrings = s.removePrefix("[[").removeSuffix("]]").split("],[")

        for (innerListString in innerListStrings) {
            val innerList = ArrayList<String>()
            // Split each inner list string by commas and remove quotes
            val strings = innerListString.split(",")
            for (str in strings) {
                innerList.add(str.trim().removeSurrounding("\""))
            }
            outerList.add(innerList)
        }
        return outerList
    }

    private fun arrayListOfListsToArrayOfArrays(list: ArrayList<ArrayList<String>>): Array<Array<String>> {
        // Create an array of the correct size
        val arrayOfArrays = Array(list.size) { emptyArray<String>() }

        for (i in list.indices) {
            // Convert each inner ArrayList to an Array and assign it
            arrayOfArrays[i] = list[i].toTypedArray()
        }
        return arrayOfArrays
    }

    private fun getTextFromUrl(urlString: String): String {
        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val content = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                content.append(line)
                content.append("\n")
                line = reader.readLine()
            }
            content.toString()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

}