package com.mao.maoweather.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mao.maoweather.R;
import com.mao.maoweather.db.City;
import com.mao.maoweather.db.County;
import com.mao.maoweather.db.Province;
import com.mao.maoweather.util.HttpUtils;
import com.mao.maoweather.util.MyUtils;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 毛麒添 on 2017/4/28 0028.
 * 选择地区的Fragment
 */

public class ChooseAreaFragment extends Fragment {

    //选中地区的三个级别
    public static final  int LEVEL_PROVINCE=0;
    public static final  int LEVEL_CITY=1;
    public static final  int LEVEL_COUNTY=2;

    private static final String SERVER_URL="http://guolin.tech/api/china/";

    private TextView tv_title;//选择地区页面标题
    private Button bt_back;//返回按钮
    private ListView list_view;

    private ArrayAdapter<String> adapter;//数组适配器

    private List<String> dataList=new ArrayList<>();
    //省，市，县列表
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectProvince;//选中的省份
    private City selectCity;//选中的城市

    private int currentLevel;//当前选中的级别

    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        tv_title= (TextView) view.findViewById(R.id.tv_title);
        bt_back= (Button) view.findViewById(R.id.bt_back);
        list_view= (ListView) view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        list_view.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){//查询市级的信息
                    selectProvince= provinceList.get(position);
                    queryCities();//查询所有市
                }else if(currentLevel==LEVEL_CITY){//查询县级的信息
                    selectCity= cityList.get(position);
                    queryCounties();//查询所有县
                }
            }
        });
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==LEVEL_COUNTY){
                    queryCities();//查询所有市
                }else if(currentLevel==LEVEL_CITY){
                    queryProvinces();//查询所有省
                }
            }
        });
        queryProvinces();
    }

    /**
     * 不管查询哪一级的数据，优先查询数据库，如果数据库上没有数据，才去请求服务器获取数据
     */
    //获取城市信息
    private void queryProvinces() {
        tv_title.setText("中国");
        bt_back.setVisibility(View.GONE);
        provinceList= DataSupport.findAll(Province.class);//获取数据库中的数据
        if(provinceList.size()>0){//如果数据库中有数据
            dataList.clear();
            for (Province province: provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else {//没有数据集则请求网络
            queryFromServer(SERVER_URL,"province");
        }
    }

    /**
     * 根据传入的地址和字段请求网络获取省市县数据
     * @param serverUrl 地址
     * @param type 字段
     */
    private void queryFromServer(String serverUrl, final String type) {
          showProgressDialog();
        Log.w("毛麒添","开始请求数据");
        HttpUtils.sendOkHttpRquest(serverUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                   //加载失败，在主线程更新UI
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr=response.body().string();
                Log.w("毛麒添",responseStr);
                boolean result=false;//是否请求成功标识
                if("province".equals(type)){
                    result=MyUtils.handleProvinceResponse(responseStr);
                }else if("city".equals(type)){
                    result=MyUtils.handleCityResponse(responseStr,selectProvince.getId());
                }else if("county".equals(type)){
                    result=MyUtils.handleCountyResponse(responseStr,selectCity.getId());
                    Log.w("result", String.valueOf(result));
                }
                if(result){//如果数据解析成功
                 getActivity().runOnUiThread(new Runnable() {//运行在主线程更新UI。显示数据
                     @Override
                     public void run() {
                         closeProgressDialog();
                         if("province".equals(type)){
                            queryProvinces();
                         }else if("city".equals(type)){
                             queryCities();
                         }else if("county".equals(type)){
                            queryCounties();
                         }
                     }
                 });
                }
            }
        });
    }
    //关闭Dialog
    private void closeProgressDialog() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    //关闭Dialog
    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //获取城市信息
    private void queryCities() {
        tv_title.setText(selectProvince.getProvinceName());
        bt_back.setVisibility(View.VISIBLE);
        //获取数据库中的数据
        cityList=DataSupport.where("provinceid = ?",String.valueOf(selectProvince.getId())).find(City.class);
        if(cityList.size()>0){//如果数据库中有数据
            dataList.clear();
            for (City city: cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else {//没有数据集则请求网络
            int provinceCode=selectProvince.getProvinceCode();
            String address=SERVER_URL + provinceCode;//请求地址
            Log.w("address",address+"");
            queryFromServer(address,"city");
        }

    }
    //获取县信息
    private void queryCounties() {
        tv_title.setText(selectProvince.getProvinceName());
        Log.w("毛麒添","查询所有县");
        bt_back.setVisibility(View.VISIBLE);
        countyList= DataSupport.where("cityid = ?",String.valueOf(selectCity.getId())).find(County.class);//获取数据库中的数据
        if(countyList.size()>0){//如果数据库中有数据
            Log.w("毛麒添","查询所有县1111");
            dataList.clear();
            for (County county: countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            list_view.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else {//没有数据集则请求网络
            Log.w("毛麒添","查询所有县2222");
            int provinceCode=selectProvince.getProvinceCode();
            Log.w("provinceCode",provinceCode+"");
            int cityCode=selectCity.getCityCode();
            Log.w("cityCode",cityCode+"");
            String address =SERVER_URL+provinceCode+ "/"+cityCode;
            Log.w("address",address+"");
            queryFromServer(address,"county");
        }
    }
}
