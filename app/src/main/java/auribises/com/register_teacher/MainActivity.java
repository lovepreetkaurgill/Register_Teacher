package auribises.com.register_teacher;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    @InjectView(R.id.editTextName)
    EditText eTxtName;

    @InjectView(R.id.editTextPhone)
    EditText eTxtPhone;

    @InjectView(R.id.editTextEmail)
    EditText eTxtEmail;

    @InjectView(R.id.editTextBirth_date)
    EditText eTxtBirth_date;

    @InjectView(R.id.editTextAddress)
    EditText eTxtAddress;

    @InjectView(R.id.editTextQualification)
    EditText eTxtQualification;

    @InjectView(R.id.editTextExperience)
    EditText eTxtExperience;


    @InjectView(R.id.radioButtonMale)
    RadioButton rbMale;

    @InjectView(R.id.radioButtonFemale)
    RadioButton rbFemale;

    ArrayAdapter<String> adapter;

    @InjectView(R.id.buttonRegister)
    Button btnSubmit;

    Register_Teacher register_teacher, rcvRegisterTeacher;

    ContentResolver resolver;

    boolean updateMode;

    RequestQueue requestQueue;

    ProgressDialog progressDialog;

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    DatePickerDialog datePickerDialog;

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

            eTxtBirth_date.setText(i+"/"+(i1+1)+"/"+i2);

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        eTxtName = (EditText)findViewById(R.id.editTextName);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        register_teacher = new Register_Teacher();

        rbMale.setOnCheckedChangeListener(this);
        rbFemale.setOnCheckedChangeListener(this);

        resolver = getContentResolver();

        requestQueue = Volley.newRequestQueue(this);

        Intent rcv = getIntent();
        updateMode = rcv.hasExtra("keyregisterteacher");


        if(updateMode){
            rcvRegisterTeacher = (Register_Teacher) rcv.getSerializableExtra("keyregisterteacher");
            //Log.i("test", Register_Teacher.toString());
            eTxtName.setText(rcvRegisterTeacher.getName());
            eTxtPhone.setText(rcvRegisterTeacher.getPhone());
            eTxtEmail.setText(rcvRegisterTeacher.getEmail());
            eTxtBirth_date.setText(rcvRegisterTeacher.getBirth_date());
            eTxtAddress.setText(rcvRegisterTeacher.getAddress());
            eTxtQualification.setText(rcvRegisterTeacher.getQualification());
            eTxtExperience.setText(rcvRegisterTeacher.getExperience());

            if(rcvRegisterTeacher.getGender().equals("Male")){
                rbMale.setChecked(true);
            }else{
                rbFemale.setChecked(true);
            }

            btnSubmit.setText("Update");
        }
    }

    boolean isNetworkConected(){

        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        //Log.i("insertIntoCloud", Register_Teacher.toString() );
        return (networkInfo!=null && networkInfo.isConnected());

    }

    public void clickHandler(View view){
        if(view.getId() == R.id.buttonRegister){
            register_teacher.setName(eTxtName.getText().toString().trim());
            register_teacher.setPhone(eTxtPhone.getText().toString().trim());
            register_teacher.setEmail(eTxtEmail.getText().toString().trim());
            register_teacher.setBirth_date(eTxtBirth_date.getText().toString().trim());
            register_teacher.setAddress(eTxtAddress.getText().toString().trim());
            register_teacher.setQualification(eTxtQualification.getText().toString().trim());
            register_teacher.setExperience(eTxtExperience.getText().toString().trim());



            //insertIntoDB();

            if(validateFields()) {
                if (isNetworkConected())
                    insertIntoCloud();
                else
                    Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, "Please correct Input", Toast.LENGTH_LONG).show();
            }
        }
    }

    void insertIntoCloud(){

        String url="";

        if(!updateMode){
            url = Util.INSERT_REGISTERTEACHER_PHP;
        }else{
            url = Util.UPDATE_REGISTERTEACHER_PHP;
        }

        progressDialog.show();

        // Volley String Request
        /*StringRequest request = new StringRequest(Request.Method.GET, Util.INSERT_STUDENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"Response: "+response,Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"Some Error"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });*/

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("test",response.toString());
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");

                    if(success == 1){
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();

                        if(updateMode)
                            finish();

                    }else{
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i("test",e.toString());
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,"Some Exception",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.i("test",error.toString());
                Toast.makeText(MainActivity.this,"Some Error"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                //Log.i("test", Register_Teacher.toString() );
                if(updateMode)
                    map.put("id",String.valueOf(rcvRegisterTeacher.getId()));

                map.put("name", register_teacher.getName());
                map.put("phone", register_teacher.getPhone());
                map.put("email", register_teacher.getEmail());
                map.put("gender", register_teacher.getGender());
                map.put("birth_date", register_teacher.getBirth_date());
                map.put("address", register_teacher.getAddress());
                map.put("qualification", register_teacher.getQualification());
                map.put("experience", register_teacher.getExperience());
                return map;
            }
        };

        requestQueue.add(request); // execute the request, send it ti server

        clearFields();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();

        if(b) {
            if (id == R.id.radioButtonMale) {
                register_teacher.setGender("Male");
            } else {
                register_teacher.setGender("Female");
            }
        }
    }

    void insertIntoDB(){

        ContentValues values = new ContentValues();

        values.put(Util.COL_NAME, register_teacher.getName());
        values.put(Util.COL_PHONE, register_teacher.getPhone());
        values.put(Util.COL_EMAIL, register_teacher.getEmail());
        values.put(Util.COL_BIRTH_DATE, register_teacher.getBirth_date());
        values.put(Util.COL_GENDER, register_teacher.getGender());
        values.put(Util.COL_ADDRESS, register_teacher.getAddress());
        values.put(Util.COL_QUALIFICATION, register_teacher.getQualification());
        values.put(Util.COL_EXPERIENCE, register_teacher.getExperience());

        if(!updateMode){
            Uri dummy = resolver.insert(Util.REGISTERTEACHER_URI,values);
            Toast.makeText(this, register_teacher.getName()+ " Registered Successfully "+dummy.getLastPathSegment(),Toast.LENGTH_LONG).show();

            // Log.i("Insert", Register_Teacher.toString());

            clearFields();
        }else{
            String where = Util.COL_ID + " = "+ rcvRegisterTeacher.getId();
            int i = resolver.update(Util.REGISTERTEACHER_URI,values,where,null);
            if(i>0){
                Toast.makeText(this,"Updation Successful",Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }

    void clearFields(){
        eTxtName.setText("");
        eTxtPhone.setText("");
        eTxtEmail.setText("");
        eTxtBirth_date.setText("");
        eTxtAddress.setText("");
        eTxtQualification.setText("");
        eTxtExperience.setText("");
        rbMale.setChecked(false);
        rbFemale.setChecked(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0,101,0,"All Teacher");


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == 101){
            Intent i = new Intent(MainActivity.this, AllRegister_TeacherActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    boolean validateFields(){
        boolean flag = true;

        if(register_teacher.getName().isEmpty()){
            flag = false;
            eTxtName.setError("Please Enter Name");
        }

        if(register_teacher.getPhone().isEmpty()){
            flag = false;
            eTxtPhone.setError("Please Enter Phone");
        }else{
            if(register_teacher.getPhone().length()<10){
                flag = false;
                eTxtPhone.setError("Please Enter 10 digits Phone Number");
            }
        }

        if(register_teacher.getEmail().isEmpty()){
            flag = false;
            eTxtEmail.setError("Please Enter Email");
        }else{
            if(!(register_teacher.getEmail().contains("@") && register_teacher.getEmail().contains("."))){
                flag = false;
                eTxtEmail.setError("Please Enter correct Email");
            }
        }
        if(register_teacher.getAddress().isEmpty()){
            flag = false;
            eTxtAddress.setError("Please Enter Address");
        }

        if(register_teacher.getQualification().isEmpty()){
            flag = false;
            eTxtQualification.setError("Please Enter Qualification");
        }

        if(register_teacher.getExperience().isEmpty()){
            flag = false;
            eTxtExperience.setError("Please Enter Experience");
        }else{
            if(register_teacher.getPhone().length()<2){
                flag = false;
                eTxtExperience.setError("Please Enter 2 digits Experience");
            }
        }

        return flag;

    }


    void showDatePicker(View view){

        Calendar calendar = Calendar.getInstance();
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        int mm = calendar.get(Calendar.MONTH);
        int yy = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(this,dateSetListener,yy,mm,dd);
        datePickerDialog.show();

    }

}

