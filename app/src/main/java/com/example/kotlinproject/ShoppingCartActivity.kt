package com.example.kotlinproject

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.payumoney.core.PayUmoneyConstants
import com.payumoney.core.PayUmoneySdkInitializer.PaymentParam
import com.payumoney.core.entity.TransactionResponse
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import com.payumoney.sdkui.ui.utils.ResultModel
import com.squareup.picasso.Picasso
import java.lang.reflect.Type
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class ShoppingCartActivity : AppCompatActivity() {

    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_cart_layout)

        val name_list = ArrayList<User>();
        name_list.addAll(getArrayList("array_list"))

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ShoppingCartAdapter(applicationContext, name_list)
        recyclerView.adapter = adapter

        val totalPrice = findViewById<TextView>(R.id.totalPrice)
        totalPrice.text = "Total Price : Rs. " + totalPrice(name_list).toString()


        val checkout = findViewById<Button>(R.id.button)

        checkout.setOnClickListener(){
            startpay(totalPrice(name_list).toString())
        }


    }

    fun totalPrice(name_list: ArrayList<User>): Int {
        var total = 0
        for (i in name_list) {
            total = (total + i.price).toInt()
        }
        return total
    }


    fun getArrayList(key: String?): ArrayList<User> {
        val prefs: SharedPreferences = this.getSharedPreferences(
                sharedPrefFile,
                Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = prefs.getString(key, null)
        val type: Type = object : TypeToken<ArrayList<User>>() {}.getType()
        return gson.fromJson(json, type)
    }


    fun startpay(amt: String) {

        val builder = PaymentParam.Builder()

        val txnId = "0nf7" + System.currentTimeMillis()

        builder.setAmount(amt) // Payment amount
            .setTxnId(txnId) // Transaction ID
            .setPhone("9762929889") // User Phone number
            .setProductName("Padharo") // Product Name or description
            .setFirstName("Jainesh") // User First name
            .setEmail("jaineshkothari007@gamil.comm") // User Email ID
            .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php") // Success URL (surl)
            .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php") //Failure URL (furl)
            .setUdf1("")
            .setUdf2("")
            .setUdf3("")
            .setUdf4("")
            .setUdf5("")
            .setUdf6("")
            .setUdf7("")
            .setUdf8("")
            .setUdf9("")
            .setUdf10("")
            .setIsDebug(true) // Integration environment - true (Debug)/ false(Production)
            .setKey("KAjebck6") // Merchant key
            .setMerchantId("6330793")
        try {

            var mPaymentParams = builder.build()
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, this, R.style.AppTheme_Green, false);


        } catch (e: Exception) {

        }
    }


    private fun calculateServerSideHashAndInitiatePayment1(paymentParam: PaymentParam): PaymentParam? {
        val stringBuilder = StringBuilder()
        val params = paymentParam.params
        stringBuilder.append(params[PayUmoneyConstants.KEY]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.TXNID]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.AMOUNT]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.PRODUCT_INFO]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.FIRSTNAME]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.EMAIL]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.UDF1]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.UDF2]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.UDF3]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.UDF4]).append("|")
        stringBuilder.append(params[PayUmoneyConstants.UDF5]).append("||||||")
        stringBuilder.append("Y2VKZbC9aq")
        val hash: String = hashCal(stringBuilder.toString())
        paymentParam.setMerchantHash(hash)
        return paymentParam
    }


    fun hashCal(str: String): String {
        val hashseq = str.toByteArray()
        val hexString = java.lang.StringBuilder()
        try {
            val algorithm: MessageDigest = MessageDigest.getInstance("SHA-512")
            algorithm.reset()
            algorithm.update(hashseq)
            val messageDigest: ByteArray = algorithm.digest()
            for (aMessageDigest in messageDigest) {
                val hex = Integer.toHexString(0xFF and aMessageDigest.toInt())
                if (hex.length == 1) {
                    hexString.append("0")
                }
                hexString.append(hex)
            }
        } catch (ignored: NoSuchAlgorithmException) {
        }
        return hexString.toString()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result Code is -1 send from Payumoney activity
        Log.d("PaymentActivity", "request code $requestCode resultcode $resultCode")
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {

            val transactionResponse: TransactionResponse? = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE)
            val resultModel: ResultModel? = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT)

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                } else {
                    //Failure Transaction
                }
            } else if (resultModel != null && resultModel.error != null) {
                Log.d("==>>", "Error response : " + resultModel.error.transactionResponse)
            } else {
                Log.d("===>>", "Both objects are null!")
            }
        }
    }

}


class ShoppingCartAdapter(private val mContext: Context, var items: ArrayList<User>) : RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingCartHolder {
        lateinit var carViewHolder: ShoppingCartHolder
        carViewHolder = ShoppingCartHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.shooping_cart_adapter,
                        parent,
                        false
                )
        )
        return carViewHolder
    }

    override fun onBindViewHolder(holder: ShoppingCartHolder, position: Int) {
        holder.initialize(items.get(position), mContext)

    }

    class ShoppingCartHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name = itemView.findViewById<TextView>(R.id.title)
        var price = itemView.findViewById<TextView>(R.id.price)
        var previewIcon = itemView.findViewById<ImageView>(R.id.imageView)


        fun initialize(item: User, mContext: Context) {
            name.text = item.name
            price.text = "Rs. : " + item.price
            Picasso.get().load(item.image).into(previewIcon)
        }

    }

}