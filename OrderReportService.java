package com.example.orders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderReportService {

  public static final int maxRetries = 3;
  private Map<String, Double> Cache = new HashMap<>();
  private String lastErr;

  public OrderReportService() {
  }

  public String Proc(String file, String customerType, boolean flag) {
    List<String[]> rows = new ArrayList<>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String l;
      while ((l = br.readLine()) != null) {
        rows.add(l.split(","));
      }
    } catch (Exception e) {
      lastErr = e.getMessage();
    }

    String report = "";
    double total = 0;
    for (String[] r : rows) {
      if (r.length > 3) {
        if (r[2] != null) {
          if (customerType == "VIP") {
            if (Double.parseDouble(r[3]) > 1000) {
              double d = Double.parseDouble(r[3]) * 0.85;
              total = total + d;
              report = report + r[0] + ";" + r[1] + ";" + d + "\n";
            } else {
              double d = Double.parseDouble(r[3]) * 0.95;
              total = total + d;
              report = report + r[0] + ";" + r[1] + ";" + d + "\n";
            }
          } else {
            double d = Double.parseDouble(r[3]);
            if (d > 5000) {
              d = d * 0.97;
            }
            total = total + d;
            report = report + r[0] + ";" + r[1] + ";" + d + "\n";
          }
        }
      }
    }

    double tax = total * 0.21;
    report = report + "TOTAL;" + total + "\n" + "TAX;" + tax + "\n";

    if (flag) {
      for (int i = 0; i < maxRetries; i++) {
        try {
          FileWriter fw = new FileWriter("report-" + System.currentTimeMillis() + ".csv");
          fw.write(report);
          fw.close();
          break;
        } catch (Exception ex) {
          System.out.println("retry " + i);
        }
      }
    }

    Cache.put(file, total);
    return report;
  }

  public double getCached(String file) {
    return Cache.get(file);
  }

  public String getLastErr() {
    return lastErr;
  }
}
