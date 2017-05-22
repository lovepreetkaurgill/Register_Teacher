package auribises.com.register_teacher;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class Register_TeacherAdapter extends ArrayAdapter<Register_Teacher> {

    Context context;
    int resource;
    ArrayList<Register_Teacher> registerteacherList,tempList;

    public Register_TeacherAdapter(Context context, int resource, ArrayList<Register_Teacher> objects) {
        super(context, resource, objects);

        this.context = context;
        this.resource = resource;
        registerteacherList = objects;
        tempList = new ArrayList<>();
        tempList.addAll(registerteacherList);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(resource,parent,false);

        TextView txtName = (TextView)view.findViewById(R.id.textViewName);
        TextView txtGender = (TextView)view.findViewById(R.id.textViewGender);

        Register_Teacher register_teacher = registerteacherList.get(position);
        txtName.setText(register_teacher.getName());
        //txtGender.setText(student.getGender());
        txtGender.setText(String.valueOf(register_teacher.getId()));

        Log.i("Test", register_teacher.toString());

        return view;
    }

    public void filter(String str){

        registerteacherList.clear();

        if(str.length()==0){
            registerteacherList.addAll(tempList);
        }else{
            for(Register_Teacher r : tempList){
                if(r.getName().toLowerCase().contains(str.toLowerCase())){
                    registerteacherList.add(r);
                }
            }
        }

        notifyDataSetChanged();
    }
}
