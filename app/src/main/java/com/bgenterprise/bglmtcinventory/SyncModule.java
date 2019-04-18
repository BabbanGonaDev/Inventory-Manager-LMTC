package com.bgenterprise.bglmtcinventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SyncModule {

    private static String InternetLink = "http://4ed841db.ngrok.io";

    public static class SyncDownInventory03T extends AsyncTask<String, String, String> {
        //This function syncs down the Inventory03T for the respective LMD.
        @SuppressLint("StaticFieldLeak")
        private Context mCtx;
        InventoryDBHandler inventoryDBHandler;

        SyncDownInventory03T(Context context) {
            this.mCtx = context;
        }

        String GetLastDateOfSync() {
            InventoryDBHandler db = new InventoryDBHandler(mCtx);
            return db.GetLastLMLSyncDate();
        }

        @Override
        protected String doInBackground(String... strings) {
            //This is the staffID of the FOD that we will use to link the LMDs and the FODs.
            String staffID = strings[0];
            String lastLMLSyncDate = GetLastDateOfSync();
            inventoryDBHandler = new InventoryDBHandler(mCtx);

            //Create a ArrayList
            ArrayList<HashMap<String, String>> wordList;
            wordList = new ArrayList<>();

            //Create a HashMap of the needed variables.
            HashMap<String, String> map = new HashMap<>();
            HttpURLConnection httpURLConnection = null;
            map.put("staff_id", staffID);
            map.put("last_date", lastLMLSyncDate);
            wordList.add(map);

            try {
                URL url = new URL(InternetLink + "/inventory/lmtc_fetch_inventory03t.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string = URLEncoder.encode("staff_id", "UTF-8") + "=" + URLEncoder.encode(staffID, "UTF-8") + "&" +
                        URLEncoder.encode("last_date", "UTF-8") + "=" + URLEncoder.encode(lastLMLSyncDate, "UTF-8");
                Log.d("data_string", "" + data_string);
                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                httpURLConnection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", String.valueOf(result));
                    try {
                        JSONArray arr = new JSONArray(result + "");
                        Log.d("result", arr + "");
                        inventoryDBHandler.updateInventory03T(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Operation failed, kindly check your internet connection";
                    }
                } else {
                    return ("Sync failed due to internal error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

        }
    }

    public static class SyncUpInventory03T extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        private Context context;
        SessionManager session;
        InventoryDBHandler inventoryDBHandler;
        ArrayList wordList;

        SyncUpInventory03T(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            inventoryDBHandler = new InventoryDBHandler(context);
            wordList = inventoryDBHandler.getInventory03TRecords();
            Log.d("HERE", wordList + " " + wordList.size());
            if (wordList.size() < 1) return "done";
            Gson gson = new GsonBuilder().create();
            String word_list = gson.toJson(wordList);
            HttpURLConnection httpURLConnection = null;

            try {
//                initialize URL
                URL url = new URL(InternetLink + "/inventory/lmtc_insert_inventory03t.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
//                set request method as 'POST'
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
//                initialize output stream
                OutputStream outputStream = httpURLConnection.getOutputStream();
//                initialize buffered writer to write to online DB
                BufferedWriter bufferedWriter;
                bufferedWriter = new BufferedWriter
                        (new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string;
//                encode upload information to UTF-8 standard character set format
                data_string = URLEncoder.encode("wordList", "UTF-8") + "=" + URLEncoder.encode(word_list, "UTF-8");
                bufferedWriter.write(data_string);
//                flush the buffer
                bufferedWriter.flush();
//                close the buffer
                bufferedWriter.close();
//                close the output stream
                outputStream.close();
//                connect to the internet
                httpURLConnection.connect();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
//                get URL connection reponse code
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
//                do this is code denotes a positive connection
                if (response_code == HttpURLConnection.HTTP_OK) {
//                    initialize input stream
                    InputStream input = httpURLConnection.getInputStream();
//                    initialize buffered reader
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    use string builder to store response values
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", "" + result);

                    try {
                        JSONArray arr = new JSONArray(String.valueOf(result));
                        Log.d("array", arr + "");
//                        update SQLite DB sync status to the online DB status
                        inventoryDBHandler.updateSyncStatus(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "All records have been Synced";
                    }
                } else {
                    return ("Sync failed due to a network error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

        }

    }

    public static class SyncDownPriceGroupT extends AsyncTask<String, String, String> {
        @SuppressLint("StaticFieldLeak")
        Context context;
        InvoiceDBHandler invoiceDBHandler;

        SyncDownPriceGroupT(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            invoiceDBHandler = new InvoiceDBHandler(context);
            String staff_id = strings[0];
            HttpURLConnection httpURLConnection = null;
            Gson gson = new GsonBuilder().create();
            String staffID = gson.toJson(staff_id);
            Log.d("staffID", "" + staffID);
            try {
                URL url = new URL(InternetLink + "/inventory/priceGroupT_syncDown");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string = URLEncoder.encode("staff_id", "UTF-8") + "=" + URLEncoder.encode(staffID, "UTF-8");
                Log.d("data_string", "" + data_string);
                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                httpURLConnection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }

                    try {
                        JSONArray arr = new JSONArray(result + "");
                        Log.d("result", arr + "");
                        invoiceDBHandler.updatePriceGroupT(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Operation failed, kindly check your internet connection";
                    }

                } else {
                    return ("Sync failed due to internal error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

        }
    }

    public static class SyncDownPriceT extends AsyncTask<String, String, String> {
        @SuppressLint("StaticFieldLeak")
        Context ctx;
        InvoiceDBHandler invoiceDBHandler;

        SyncDownPriceT(Context context) {
            this.ctx = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            invoiceDBHandler = new InvoiceDBHandler(ctx);
            String staff_id = strings[0];
            HttpURLConnection httpURLConnection = null;
            Gson gson = new GsonBuilder().create();
            String staffID = gson.toJson(staff_id);

            try {
                URL url = new URL(InternetLink + "/inventory/priceT_SyncDown.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string = URLEncoder.encode("staff_id", "UTF-8") + "=" + URLEncoder.encode(staffID, "UTF-8");
                Log.d("data_string", "" + data_string);
                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                httpURLConnection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", String.valueOf(result));
                    try {
                        JSONArray arr = new JSONArray(result + "");
                        Log.d("result", arr + "");
                        invoiceDBHandler.updatePriceT(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Sync failed due to internal error";
                    }

                } else {
                    return ("Operation failed, kindly check your internet connection");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

        }
    }

    public static class SyncDownHoldingCostT extends AsyncTask<String, String, String> {
        @SuppressLint("StaticFieldLeak")
        Context ctx;
        InventoryDBHandler inventoryDBHandler;

        SyncDownHoldingCostT(Context context) {
            this.ctx = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            inventoryDBHandler = new InventoryDBHandler(ctx);
            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL(InternetLink + "/inventory/SyncDownHoldingCostT.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", String.valueOf(result));
                    try {
                        JSONArray arr = new JSONArray(result + "");
                        Log.d("result", arr + "");
                        inventoryDBHandler.updateHoldingCostT(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Operation failed, kindly check your internet connection";
                    }

                } else {
                    return ("Sync failed due to internal error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }
    }

    public static class RefreshLMDInvoiceValueT extends AsyncTask<String, String, String> {
        @SuppressLint("StaticFieldLeak")
        Context ctx;
        InvoiceDBHandler invoiceDBHandler;

        RefreshLMDInvoiceValueT(Context context) {
            this.ctx = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            invoiceDBHandler = new InvoiceDBHandler(ctx);
            String staff_id = strings[0];
            HttpURLConnection httpURLConnection = null;
            Gson gson = new GsonBuilder().create();
            String staffID = gson.toJson(staff_id);

            try {
                URL url = new URL(InternetLink + "/inventory/RefreshLMDInvoiceValueT.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string = URLEncoder.encode("staff_id", "UTF-8") + "=" + URLEncoder.encode(staffID, "UTF-8");
                Log.d("data_string", "" + data_string);
                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                httpURLConnection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }

            try {
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", String.valueOf(result));
                    try {
                        JSONArray arr = new JSONArray(result + "");
                        Log.d("result", arr + "");
                        invoiceDBHandler.RefreshLMDInvoiceValueT(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Operation failed, kindly check your internet connection";
                    }

                } else {
                    return ("Sync failed due to internal error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

        }
    }

    public static class SyncDownLeadTimeT extends AsyncTask<String, String, String> {
        @SuppressLint("StaticFieldLeak")
        Context ctx;
        InvoiceDBHandler invoiceDBHandler;

        SyncDownLeadTimeT(Context context) {
            this.ctx = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            invoiceDBHandler = new InvoiceDBHandler(ctx);
            String staff_id = strings[0];
            HttpURLConnection httpURLConnection = null;
            Gson gson = new GsonBuilder().create();
            String staffID = gson.toJson(staff_id);

            try {
                URL url = new URL(InternetLink + "/inventory/SyncDownLeadTimeT.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string = URLEncoder.encode("staff_id", "UTF-8") + "=" + URLEncoder.encode(staffID, "UTF-8");
                Log.d("data_string", "" + data_string);
                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                httpURLConnection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }

            try {
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", String.valueOf(result));
                    try {
                        JSONArray arr = new JSONArray(result + "");
                        Log.d("result", arr + "");
                        invoiceDBHandler.updateLeadTimeT(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Operation failed, kindly check your internet connection";
                    }

                } else {
                    return ("Sync failed due to internal error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

        }
    }

    public static class updateLMDT extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        Context ctx;
        LMD_DBHandler lmd_dbHandler;

        updateLMDT(Context context) {
            this.ctx = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            lmd_dbHandler = new LMD_DBHandler(ctx);
            String staff_id = strings[0];
            HttpURLConnection httpURLConnection = null;
            Gson gson = new GsonBuilder().create();
            String staffID = gson.toJson(staff_id);

            try {
                URL url = new URL(InternetLink + "/inventory/updateLMDT.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string = URLEncoder.encode("staff_id", "UTF-8") + "=" + URLEncoder.encode(staffID, "UTF-8");
                Log.d("data_string", "" + data_string);
                bufferedWriter.write(data_string);
                bufferedWriter.flush();
                bufferedWriter.close();
                httpURLConnection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", String.valueOf(result));
                    try {
                        JSONArray arr = new JSONArray(result + "");
                        Log.d("result", arr + "");
                        lmd_dbHandler.updateLMDT(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Operation failed, kindly check your internet connection";
                    }
                } else {
                    return ("Sync failed due to internal error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

        }
    }

    public static class SyncUpLMDInvValueT extends AsyncTask<Void, Void, String> {
        @SuppressLint("StaticFieldLeak")
        Context ctx;
        ArrayList wordList;
        InvoiceDBHandler invoiceDBHandler;

        SyncUpLMDInvValueT(Context context) {
            this.ctx = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            invoiceDBHandler = new InvoiceDBHandler(ctx);
            wordList = invoiceDBHandler.uploadLMDInvValueT();
            Log.d("HERE", wordList + " " + wordList.size());
            if (wordList.size() < 1) return "done";
            Gson gson = new GsonBuilder().create();
            String word_list = gson.toJson(wordList);
            HttpURLConnection httpURLConnection = null;

            try {
//                initialize URL
                URL url = new URL(InternetLink + "/inventory/SyncUpLMDInvValueT");
                httpURLConnection = (HttpURLConnection) url.openConnection();
//                set request method as 'POST'
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
//                initialize output stream
                OutputStream outputStream = httpURLConnection.getOutputStream();
//                initialize buffered writer to write to online DB
                BufferedWriter bufferedWriter;
                bufferedWriter = new BufferedWriter
                        (new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string;
//                encode upload information to UTF-8 standard character set format
                data_string = URLEncoder.encode("wordList", "UTF-8") + "=" + URLEncoder.encode(word_list, "UTF-8");
                bufferedWriter.write(data_string);
//                flush the buffer
                bufferedWriter.flush();
//                close the buffer
                bufferedWriter.close();
//                close the output stream
                outputStream.close();
//                connect to the internet
                httpURLConnection.connect();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
//                get URL connection reponse code
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
//                do this is code denotes a positive connection
                if (response_code == HttpURLConnection.HTTP_OK) {
//                    initialize input stream
                    InputStream input = httpURLConnection.getInputStream();
//                    initialize buffered reader
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    use string builder to store response values
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", "" + result);

                    try {
                        JSONArray arr = new JSONArray(String.valueOf(result));
                        Log.d("array", arr + "");
//                        update SQLite DB sync status to the online DB status
                        invoiceDBHandler.updateLMDInvValueTSyncStatus(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "All records have been Synced";
                    }
                } else {
                    return ("Sync failed due to a network error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

        }
    }

    public static class SyncUpReceiptTable extends AsyncTask<Void, Void, String> {
        @SuppressLint("StaticFieldLeak")
        Context ctx;
        ArrayList wordList;
        ReceiptDBHandler receiptDBHandler;

        SyncUpReceiptTable(Context context) {
            this.ctx = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            receiptDBHandler = new ReceiptDBHandler(ctx);
            wordList = receiptDBHandler.uploadReceiptT();
            Log.d("HERE", wordList + " " + wordList.size());
            if (wordList.size() < 1) return "done";
            Gson gson = new GsonBuilder().create();
            String word_list = gson.toJson(wordList);
            HttpURLConnection httpURLConnection = null;

            try {
//                initialize URL
                URL url = new URL(InternetLink + "/inventory/SyncUpReceiptTable");
                httpURLConnection = (HttpURLConnection) url.openConnection();
//                set request method as 'POST'
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
//                initialize output stream
                OutputStream outputStream = httpURLConnection.getOutputStream();
//                initialize buffered writer to write to online DB
                BufferedWriter bufferedWriter;
                bufferedWriter = new BufferedWriter
                        (new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string;
//                encode upload information to UTF-8 standard character set format
                data_string = URLEncoder.encode("wordList", "UTF-8") + "=" + URLEncoder.encode(word_list, "UTF-8");
                bufferedWriter.write(data_string);
//                flush the buffer
                bufferedWriter.flush();
//                close the buffer
                bufferedWriter.close();
//                close the output stream
                outputStream.close();
//                connect to the internet
                httpURLConnection.connect();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
//                get URL connection reponse code
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
//                do this is code denotes a positive connection
                if (response_code == HttpURLConnection.HTTP_OK) {
//                    initialize input stream
                    InputStream input = httpURLConnection.getInputStream();
//                    initialize buffered reader
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    use string builder to store response values
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", "" + result);

                    try {
                        JSONArray arr = new JSONArray(String.valueOf(result));
                        Log.d("array", arr + "");
//                        update SQLite DB sync status to the online DB status
                        receiptDBHandler.updateReceiptTableSyncStatus(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "All records have been Synced";
                    }
                } else {
                    return ("Sync failed due to a network error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

        }
    }

    public static class SyncUpRestockT extends AsyncTask<Void, Void, String> {
        @SuppressLint("StaticFieldLeak")
        Context ctx;
        ArrayList wordList;
        RestockDBHandler restockDBHandler;

        SyncUpRestockT(Context context) {
            this.ctx = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            restockDBHandler = new RestockDBHandler(ctx);
            wordList = restockDBHandler.uploadRestockT();
            Log.d("HERE", wordList + " " + wordList.size());
            if (wordList.size() < 1) return "done";
            Gson gson = new GsonBuilder().create();
            String word_list = gson.toJson(wordList);
            HttpURLConnection httpURLConnection = null;

            try {
//                initialize URL
                URL url = new URL(InternetLink + "/inventory/SyncUpRestockT.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
//                set request method as 'POST'
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
//                initialize output stream
                OutputStream outputStream = httpURLConnection.getOutputStream();
//                initialize buffered writer to write to online DB
                BufferedWriter bufferedWriter;
                bufferedWriter = new BufferedWriter
                        (new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string;
//                encode upload information to UTF-8 standard character set format
                data_string = URLEncoder.encode("wordList", "UTF-8") + "=" + URLEncoder.encode(word_list, "UTF-8");
                bufferedWriter.write(data_string);
//                flush the buffer
                bufferedWriter.flush();
//                close the buffer
                bufferedWriter.close();
//                close the output stream
                outputStream.close();
//                connect to the internet
                httpURLConnection.connect();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
//                get URL connection reponse code
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
//                do this is code denotes a positive connection
                if (response_code == HttpURLConnection.HTTP_OK) {
//                    initialize input stream
                    InputStream input = httpURLConnection.getInputStream();
//                    initialize buffered reader
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    use string builder to store response values
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", "" + result);

                    try {
                        JSONArray arr = new JSONArray(String.valueOf(result));
                        Log.d("array", arr + "");
//                        update SQLite DB sync status to the online DB status
                        restockDBHandler.updateRestockTSyncStatus(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "All records have been Synced";
                    }
                } else {
                    return ("Sync failed due to a network error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }
    }

    public static class SyncUpTellerT extends AsyncTask<Void, Void, String> {
        @SuppressLint("StaticFieldLeak")
        Context ctx;
        ArrayList wordList;
        TellerDBHandler tellerDBHandler;

        SyncUpTellerT(Context context) {
            this.ctx = context;
        }

        @Override
        protected String doInBackground(Void... voids) {
            tellerDBHandler = new TellerDBHandler(ctx);
            wordList = tellerDBHandler.uploadTellerT();
            Log.d("HERE", wordList + " " + wordList.size());
            if (wordList.size() < 1) return "done";
            Gson gson = new GsonBuilder().create();
            String word_list = gson.toJson(wordList);
            HttpURLConnection httpURLConnection = null;

            try {
//                initialize URL
                URL url = new URL(InternetLink + "/inventory/SyncUpTellerT.php");
                httpURLConnection = (HttpURLConnection) url.openConnection();
//                set request method as 'POST'
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
//                initialize output stream
                OutputStream outputStream = httpURLConnection.getOutputStream();
//                initialize buffered writer to write to online DB
                BufferedWriter bufferedWriter;
                bufferedWriter = new BufferedWriter
                        (new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String data_string;
//                encode upload information to UTF-8 standard character set format
                data_string = URLEncoder.encode("wordList", "UTF-8") + "=" + URLEncoder.encode(word_list, "UTF-8");
                bufferedWriter.write(data_string);
//                flush the buffer
                bufferedWriter.flush();
//                close the buffer
                bufferedWriter.close();
//                close the output stream
                outputStream.close();
//                connect to the internet
                httpURLConnection.connect();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
//                get URL connection reponse code
                int response_code = Objects.requireNonNull(httpURLConnection).getResponseCode();
//                do this is code denotes a positive connection
                if (response_code == HttpURLConnection.HTTP_OK) {
//                    initialize input stream
                    InputStream input = httpURLConnection.getInputStream();
//                    initialize buffered reader
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                    use string builder to store response values
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    Log.d("result", "" + result);

                    try {
                        JSONArray arr = new JSONArray(String.valueOf(result));
                        Log.d("array", arr + "");
//                        update SQLite DB sync status to the online DB status
                        tellerDBHandler.updateTellerTSyncStatus(arr);
                        return "done";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "All records have been Synced";
                    }
                } else {
                    return ("Sync failed due to a network error.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Sync failed due to internal error. Most likely a network error";
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
        }
    }


}