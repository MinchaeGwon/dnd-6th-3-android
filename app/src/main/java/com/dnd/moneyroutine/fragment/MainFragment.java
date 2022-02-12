package com.dnd.moneyroutine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dnd.moneyroutine.R;
import com.dnd.moneyroutine.SocialLoginActivity;
import com.dnd.moneyroutine.custom.Constants;
import com.dnd.moneyroutine.custom.PreferenceManager;
import com.dnd.moneyroutine.service.RequestService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment {

    private final static String TAG = "MainFragment";

    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);

        Call<String> call = RequestService.getInstance().test(); // 토큰이 필요 없을 때 사용하는 코드
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(View v) {
        btnLogout = v.findViewById(R.id.btn_main_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutToServer();
            }
        });
    }

    // 임시 로그아웃 버튼 설정
    private void logoutToServer() {
        // 서버 구현 전이기 때문에 SharedPreferences에서만 토큰을 삭제
        removeSharedPreferences();
        afterLogoutMoveActivity();

//        String token = PreferenceManager.getToken(getContext(), Constants.tokenKey); // 토큰 가져오기
//
//        // 헤더에 토큰 추가하는 코드
//        HeaderRetrofit headerRetrofit = new HeaderRetrofit();
//        Retrofit retrofit = headerRetrofit.getTokenHeaderInstance(token);
//        RetrofitService retroService = retrofit.create(RetrofitService.class);
//
//        Call<JsonObject> call = retroService.signout();
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if(response.isSuccessful()) {
//                    // sharedPreferences가 보관하고 있던 토큰을 삭제
//                    removeSharedPreferences();
//                    afterLogoutMoveActivity();
//                    Toast.makeText(getContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Toast.makeText(getContext(), "네트워크가 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    // SharedPreferences에서 토큰 제거
    private void removeSharedPreferences() {
        PreferenceManager.removeKey(getContext(), Constants.tokenKey);
    }

    // 로그아웃 후에 어디로 이동할지 결정하는 메소드 -> SocialLoginActivity로 이동
    private void afterLogoutMoveActivity() {
        Intent intent = new Intent(getContext(), SocialLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
