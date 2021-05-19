package com.example.kotlinproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.lang.reflect.Type


class MainActivity : AppCompatActivity(), OnCarItemClickListner {

    private val client = OkHttpClient()
    private val sharedPrefFile = "kotlinsharedpreference"
    val name_list = ArrayList<User>();
    val save_data_list = ArrayList<User>();
    var item_count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        name_list.clear()
        save_data_list.clear()

        val imageView = findViewById<ImageView>(R.id.imageView)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        name_list.add(User(1, "ABC", 100.0, "https://www.thespruce.com/thmb/lvw8vfebmVIDZ6xtq8rcyN0OqdY=/3776x2832/smart/filters:no_upscale()/fruits-that-grow-in-the-shade-1388680-hero-b8fd460c4ca842bfb31440784bba31d1.jpg"))
        name_list.add(User(2, "PQR", 200.0, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRWtCprAuYoo6RgOFps7_gFPa8-X27-kTFDfw&usqp=CAU"))
        name_list.add(User(3, "XYZ", 300.0, "https://i0.wp.com/post.healthline.com/wp-content/uploads/2020/10/organic_oranges_in_a_bowl-1296x728-header.jpg?w=1155&h=1528"))
        name_list.add(User(4, "JKL", 500.0, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSxvatq63W_GGR3EyU4hpZGDLfJiaxEgKUQPw&usqp=CAU"))
        name_list.add(User(5, "ABC", 600.0, "https://cdn-prod.medicalnewstoday.com/content/images/articles/324/324431/healthiest-fruits-lemons.jpg"))
        name_list.add(User(6, "XYZ", 200.0, "https://images.everydayhealth.com/images/ordinary-fruits-with-amazing-health-benefits-09-1440x810.jpg"))
        name_list.add(User(7, "JKL", 100.0, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTh5jqfatCoKE9I4_lbXjTnjsGTucbAJl7oEA&usqp=CAU"))
        name_list.add(User(8, "PQR", 40.0, "https://www.thespruce.com/thmb/lvw8vfebmVIDZ6xtq8rcyN0OqdY=/3776x2832/smart/filters:no_upscale()/fruits-that-grow-in-the-shade-1388680-hero-b8fd460c4ca842bfb31440784bba31d1.jpg"))

        val adapter = CartAdapter(applicationContext, name_list, this)
        recyclerView.adapter = adapter

        imageView.setOnClickListener() {
            if (getArrayList("array_list").size != 0) {
                val intent = Intent(applicationContext, ShoppingCartActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent)
            } else {

            }
        }
    }

    fun run(url: String) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {

                println(response.body()?.string())

                val gson = Gson()
                val objectList = gson.fromJson(response.body()?.string(), Array<User>::class.java).asList()

                Toast.makeText(applicationContext, "" + objectList.size, Toast.LENGTH_SHORT).show()

            }
        })
    }

    override fun onItemClick(user: User, postion: Int) {
        Toast.makeText(applicationContext, "this is added to the cart", Toast.LENGTH_SHORT).show()
        save_data_list.add(user)
        saveArrayList(save_data_list, "array_list")

        val notifcation_badge = findViewById<TextView>(R.id.notifcation_badge)
        notifcation_badge.text = ""+getArrayList("array_list").size
    }


    fun saveArrayList(list: ArrayList<User>, key: String?) {

        val prefs: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    fun getArrayList(key: String?): ArrayList<User> {

        try {

            val prefs: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
            val gson = Gson()
            val json: String? = prefs.getString(key, null)
            val type: Type = object : TypeToken<ArrayList<User>>() {}.getType()
            return gson.fromJson(json, type)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Your Cart is Empty", Toast.LENGTH_SHORT).show()
            val name_list = ArrayList<User>();
            return name_list
        }

    }
    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}

data class User(val id: Int, val name: String, val price: Double, val image: String)


class CartAdapter(private val mContext: Context, var items: ArrayList<User>, var clickListner: OnCarItemClickListner) : RecyclerView.Adapter<CartAdapter.CarViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        lateinit var carViewHolder: CarViewHolder
        carViewHolder = CarViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_first, parent, false))
        return carViewHolder
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.initialize(items.get(position), clickListner, mContext)

    }

    class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var top_layout = itemView.findViewById<LinearLayout>(R.id.top_layout)
        var name = itemView.findViewById<TextView>(R.id.title)
        var price = itemView.findViewById<TextView>(R.id.price)
        var previewIcon = itemView.findViewById<ImageView>(R.id.previewIcon)
        var addtocart = itemView.findViewById<Button>(R.id.addtocart)


        fun initialize(item: User, action: OnCarItemClickListner, mContext: Context) {
            name.text = item.name
            price.text = "Rs. : " + item.price
            Picasso.get().load(item.image).into(previewIcon)

            addtocart.setOnClickListener {
                action.onItemClick(item, adapterPosition)
                addtocart.setText("Added")
            }

            top_layout.setOnClickListener() {

                val intent = Intent(mContext, SecondActivity::class.java)
                intent.putExtra("id", "" + item.id)
                intent.putExtra("name", "" + item.name)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent)

            }

        }

    }

}


interface OnCarItemClickListner {
    fun onItemClick(item: User, position: Int)
}

/* findViewById<Button>(R.id.button).setOnClickListener { view ->
     Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
             .setAction("Action", null).show()


     run("https://api.github.com/users/Evin1-/repos")
 }*/
