package com.mbv.pokket.util;

import java.util.Arrays;
import java.util.List;

/**
 * Created by arindamnath on 07/02/16.
 */
public class GraphUtil {

    private StringBuilder graph;
    private String title = "Transaction Summary";
    private String subtitle = "Total, Lent, and Borrowed: Last 6 Months";
    private String legendY = "Month";
    private List<String> legendX = Arrays.asList(new String[] {"Total", "Lent", "Borrowed"});

    public GraphUtil() {
        graph = new StringBuilder();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getGraphHTML() {
        graph.append("<html>\n");
        graph.append("  <head>\n");
        graph.append("    <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n");
        graph.append("    <script type=\"text/javascript\">\n");
        graph.append("      google.charts.load('current', {'packages':['bar']});\n");
        graph.append("      google.charts.setOnLoadCallback(drawChart);\n");
        graph.append("      function drawChart() {\n");
        graph.append("        var data = google.visualization.arrayToDataTable([\n");
        graph.append("          ['" + legendY + "'\n");
        for(String legend : legendX) {
            graph.append(",'" + legend + "'");
        }
        graph.append("],\n");
        graph.append("          ['Sep 15', 1000, 400, 200],\n"); //TODO make this dynamic
        graph.append("          ['Oct 15', 1000, 400, 200],\n");
        graph.append("          ['Nov 15', 1000, 400, 200],\n");
        graph.append("          ['Dec 15', 1170, 460, 250],\n");
        graph.append("          ['Jan 16', 660, 1120, 300],\n");
        graph.append("          ['Feb 16', 1030, 540, 350]\n");
        graph.append("        ]);\n\n");
        graph.append("        var options = {\n");
        graph.append("          chart: {\n");
        graph.append("            title: '" + title + "',\n");
        graph.append("            subtitle: '" + subtitle + "',\n");
        graph.append("          },\n");
        graph.append("          bars: 'horizontal' // Required for Material Bar Charts.\n");
        graph.append("        };\n\n");
        graph.append("        var chart = new google.charts.Bar(document.getElementById('barchart_material'));\n\n");
        graph.append("        chart.draw(data, options);\n");
        graph.append("      }\n");
        graph.append("    </script>\n");
        graph.append("  </head>\n");
        graph.append("  <body>\n");
        graph.append("    <div id=\"barchart_material\" style=\"width: 100%; height: 100%;\"></div>\n");
        graph.append("  </body>\n");
        graph.append("</html>");
        return graph.toString();
    }
}
