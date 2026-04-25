package com.example.jnidemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Déclarations des méthodes natives
    public native String helloFromJNI();
    public native int factorial(int n);
    public native String reverseString(String s);
    public native int sumArray(int[] values);
    public native int[] multiplyMatrices(int[] a, int[] b);

    // Chargement de la bibliothèque native
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Hello World JNI
        TextView tvHello = findViewById(R.id.tvHello);
        tvHello.setText(helloFromJNI());

        // 2. Section Factoriel
        final EditText etFactorial = findViewById(R.id.etFactorial);
        Button btnFactorial = findViewById(R.id.btnFactorial);
        final TextView tvFactResult = findViewById(R.id.tvFactResult);

        btnFactorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etFactorial.getText().toString().trim();
                if (!input.isEmpty()) {
                    try {
                        int n = Integer.parseInt(input);
                        int result = factorial(n);
                        if (result >= 0) {
                            tvFactResult.setText("Résultat : " + result);
                        } else if (result == -1) {
                            tvFactResult.setText("Erreur : Nombre négatif");
                        } else if (result == -2) {
                            tvFactResult.setText("Erreur : Overflow (n trop grand)");
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Veuillez entrer un nombre valide", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 3. Section Inversion de Chaîne
        final EditText etReverse = findViewById(R.id.etReverse);
        Button btnReverse = findViewById(R.id.btnReverse);
        final TextView tvReverseResult = findViewById(R.id.tvReverseResult);

        btnReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etReverse.getText().toString();
                if (!input.isEmpty()) {
                    String result = reverseString(input);
                    tvReverseResult.setText("Résultat : " + result);
                }
            }
        });

        // 4. Section Somme de Tableau
        final EditText etArray = findViewById(R.id.etArray);
        Button btnArray = findViewById(R.id.btnArray);
        final TextView tvArrayResult = findViewById(R.id.tvArrayResult);

        btnArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etArray.getText().toString().trim();
                if (!input.isEmpty()) {
                    // Séparation par espaces ou virgules
                    String[] parts = input.split("[\\s,]+");
                    int[] numbers = new int[parts.length];
                    try {
                        for (int i = 0; i < parts.length; i++) {
                            numbers[i] = Integer.parseInt(parts[i]);
                        }
                        int sum = sumArray(numbers);
                        if (sum >= -3) { // Gestion des codes d'erreurs définis en C++
                            if (sum == -1) tvArrayResult.setText("Erreur : Tableau nul");
                            else if (sum == -2) tvArrayResult.setText("Erreur : Accès éléments");
                            else if (sum == -3) tvArrayResult.setText("Erreur : Overflow somme");
                            else tvArrayResult.setText("Résultat (Somme) : " + sum);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Entrez des nombres valides (ex: 1 2 3)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 5. Section Multiplication Matricielle
        final EditText etMatrixA = findViewById(R.id.etMatrixA);
        final EditText etMatrixB = findViewById(R.id.etMatrixB);
        Button btnMatrix = findViewById(R.id.btnMatrix);
        final TextView tvMatrixResult = findViewById(R.id.tvMatrixResult);

        btnMatrix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputA = etMatrixA.getText().toString().trim();
                String inputB = etMatrixB.getText().toString().trim();

                if (!inputA.isEmpty() && !inputB.isEmpty()) {
                    try {
                        int[] a = parseMatrix(inputA);
                        int[] b = parseMatrix(inputB);

                        if (a.length == 4 && b.length == 4) {
                            int[] result = multiplyMatrices(a, b);
                            if (result != null) {
                                tvMatrixResult.setText(String.format("Résultat : [%d %d, %d %d]",
                                        result[0], result[1], result[2], result[3]));
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Chaque matrice doit avoir 4 nombres (2x2)", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Format invalide", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private int[] parseMatrix(String input) {
        String[] parts = input.split("[\\s,]+");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }
}