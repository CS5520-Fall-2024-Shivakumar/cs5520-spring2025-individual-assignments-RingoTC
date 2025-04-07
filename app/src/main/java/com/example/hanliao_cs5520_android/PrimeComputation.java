package com.example.hanliao_cs5520_android;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PrimeComputation extends AppCompatActivity {
    private TextView currentNumber;
    private TextView lastPrimeNumber;
    private Button terminateSearch;
    private Button findPrimes;
    private CheckBox pacifierSwitch;

    private Thread worker;
    private volatile boolean isSearching = false;
    private Handler mainHandler;

    private volatile int lastCurrentNumber = 3;
    private volatile int lastPrimeFound = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime_computation);

        currentNumber = findViewById(R.id.current_number_value);
        lastPrimeNumber = findViewById(R.id.last_prime_value);
        findPrimes = findViewById(R.id.find_prime_button);
        terminateSearch = findViewById(R.id.stop_finding);
        pacifierSwitch = findViewById(R.id.pacifier_switch);

        mainHandler = new Handler(Looper.getMainLooper());

        updateDisplayValues();

        findPrimes.setOnClickListener(v -> startPrimeSearch());
        terminateSearch.setOnClickListener(v -> stopPrimeSearch());
    }

    private void updateDisplayValues() {
        mainHandler.post(() -> {
            String currentText = String.valueOf(lastCurrentNumber);
            String primeText = String.valueOf(lastPrimeFound);
            
            currentNumber.setText(currentText);
            lastPrimeNumber.setText(primeText);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastCurrentNumber", lastCurrentNumber);
        outState.putInt("lastPrimeFound", lastPrimeFound);
        outState.putBoolean("isSearching", isSearching);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastCurrentNumber = savedInstanceState.getInt("lastCurrentNumber", 3);
        lastPrimeFound = savedInstanceState.getInt("lastPrimeFound", 2);
        boolean wasSearching = savedInstanceState.getBoolean("isSearching", false);
        
        updateDisplayValues();
        
        if (wasSearching) {
            startPrimeSearch();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPrimeSearch();
    }

    private void stopPrimeSearch() {
        if(!isSearching){
            return;
        }
        isSearching = false;

        if (worker != null) {
            try {
                worker.join(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            worker = null;
        }
        
        updateDisplayValues();
    }

    private void startPrimeSearch(){
        if(isSearching){
            return;
        }
        isSearching = true;

        worker = new Thread(() -> {
            int cur = lastCurrentNumber;

            while(isSearching){
                final int number2check = cur;
                lastCurrentNumber = number2check;
                
                mainHandler.post(() -> {
                    currentNumber.setText(String.valueOf(number2check));
                });

                if (isPrime(cur)) {
                    lastPrimeFound = cur;
                    int finalCur = cur;
                    mainHandler.post(() -> {
                        lastPrimeNumber.setText(String.valueOf(finalCur));
                    });
                }

                cur += 2;
            }
        });
        worker.start();
    }

    private boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }

        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (isSearching) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Stop Searching")
                .setMessage("Are you sure you want to stop searching for primes?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    stopPrimeSearch();
                    super.onBackPressed();
                })
                .setNegativeButton("No", null)
                .show();
        } else {
            super.onBackPressed();
        }
    }
}
