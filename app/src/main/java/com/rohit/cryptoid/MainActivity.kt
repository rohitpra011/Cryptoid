package com.rohit.cryptoid

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.rohit.cryptoid.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var rvAdapter:RvAdapter
    private lateinit var data:ArrayList<Model>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide();
        data= ArrayList<Model>()
        apiData
        rvAdapter=RvAdapter(this,data)
        binding.Rv.layoutManager=LinearLayoutManager(this)
        binding.Rv.adapter=rvAdapter
        binding.search.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val filterdata=ArrayList<Model>()
                    for (item in data){
                        if (item.name.lowercase(Locale.getDefault()).contains(p0.toString().lowercase(Locale.getDefault())))
                        {
                            filterdata.add(item)
                        }
                    }
                if (filterdata.isEmpty()){
                    Toast.makeText(this@MainActivity,"No data available",Toast.LENGTH_SHORT).show()
                }else{
                    rvAdapter.changeData(filterdata)
                }

            }


        })

    }

    val apiData:Unit
        get() {
            val url="https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?CMC_PRO_API_KEY=99d6dd62-2908-4aee-8590-cb7f5ceb59ad"
            val queue=Volley.newRequestQueue(this)
            val jsonObjectRequest:JsonObjectRequest=
                @SuppressLint("NotifyDataSetChanged")
                object:JsonObjectRequest(Method.GET,url,null,Response.Listener {
                        response ->
                    binding.progressBar.isVisible =false
                    try {
                        val dataArray=response.getJSONArray("data")
                        for (i in 0 until dataArray.length() )
                        {
                            val dataObject=dataArray.getJSONObject(i)
                            val symbol=dataObject.getString("symbol")
                            val name=dataObject.getString("name")
                            val quote=dataObject.getJSONObject("quote")
                            val USD=quote.getJSONObject("USD")
                            val price=String.format("$"+"%.2f",USD.getDouble("price"))
                            data.add(Model(name,symbol,price))
                        }
                        rvAdapter.notifyDataSetChanged()
                    }catch (e:Exception){
                        Toast.makeText(this,"Error in catch",Toast.LENGTH_SHORT).show()
                    }
                },Response.ErrorListener {
                    Toast.makeText(this,"Error in err lis",Toast.LENGTH_SHORT).show()
                })
                {
                    override fun getHeaders(): HashMap<String, String> {
                      //  return super.getHeaders()
                        val headers=HashMap<String,String>();
                        headers["CMC_PRO_API_KEY"]="99d6dd62-2908-4aee-8590-cb7f5ceb59ad"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        }
}