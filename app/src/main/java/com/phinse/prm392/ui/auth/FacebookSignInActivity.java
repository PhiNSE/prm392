package com.phinse.prm392.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.phinse.prm392.R;
import com.phinse.prm392.service.model.User;
import com.phinse.prm392.ui.home.HomeActivity;

import kotlin.text.Regex;

public class FacebookSignInActivity extends AppCompatActivity {
    private static final String TAG = "FacebookSignInActivity";

    private FirebaseAuth mAuth = null;
    private FirebaseFirestore db = null;

    //one tap sign in
    private GoogleSignInClient mGoogleSignInClient;
    private BeginSignInRequest signInRequest;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_sign_in);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etPhoneOrEmail = findViewById(R.id.etPhoneOrEmail);
        EditText etPass = findViewById(R.id.etPass);
        Button btnLogin2 = findViewById(R.id.btnLogin2);

        btnLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneOrEmail = etPhoneOrEmail.getText().toString();
                String password = etPass.getText().toString();
                Regex emailRegex = new Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$");
                if (phoneOrEmail.isEmpty() || !emailRegex.matches(phoneOrEmail) || password.isEmpty()) {
                    Toast.makeText(FacebookSignInActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                    return;
                }
                signUpWithFirebase(phoneOrEmail, password);
            }
        });

        ImageView ivInsta = findViewById(R.id.ivInsta);
        ivInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacebookSignInActivity.this, InstagramSignIn.class);
                startActivity(intent);
            }
        });

        setUpLoginWithGoogle();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            doAfterAuth(currentUser);
        }
    }

    private void signUpWithFirebase(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            handleCreateUserWithEmailSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(FacebookSignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void doAfterAuth(FirebaseUser user) {

        Intent intent = new Intent(FacebookSignInActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void handleCreateUserWithEmailSuccess(FirebaseUser user) {
        // Get the document reference
        DocumentReference docRef = db.collection("users").document(user.getUid());

        // Try to get the document
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, no need to create a new one
                        Log.d(TAG, "User already exists in Firestore");
                        doAfterAuth(user);
                    } else {
                        // Document does not exist, create a new one
                        Log.d(TAG, "User does not exist in Firestore");
                        // Create a new user document
                        User newUser = new User();
                        newUser.setUid(user.getUid());
                        newUser.setEmail(user.getEmail());
                        newUser.setName(user.getDisplayName());
                        newUser.setPhoneNumber(user.getPhoneNumber());
                        newUser.setProtoUrl(user.getPhotoUrl() == null ? "https://upload.wikimedia.org/wikipedia/commons/9/99/Sample_User_Icon.png" : user.getPhotoUrl().toString());
                        db.collection("users").document(user.getUid())
                                .set(newUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        doAfterAuth(user);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
                } else {
                    Log.w(TAG, "Error getting document", task.getException());
                }

            }
        });
    }

    private void setUpLoginWithGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("147293638174-4mvl60ms4arj7k4mbsbruncds82ip80f.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        findViewById(R.id.ivGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.d(TAG, "handleSignInResult:success");
            FirebaseUser user = mAuth.getCurrentUser();
            handleCreateUserWithEmailSuccess(user);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Log.w(TAG, "handleSignInResult:failed code=" + e.getStatusCode());
        }
    }

}