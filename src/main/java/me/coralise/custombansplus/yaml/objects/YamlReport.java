package me.coralise.custombansplus.yaml.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.coralise.custombansplus.CustomBansPlus;
import me.coralise.custombansplus.ClassGetter;

public class YamlReport {

    private UUID uuid;
    private String date;
    private String reporterUuid;
    private String report;
    private int reportNum;
    private boolean resolved;
    private String resolverUuid;

    private static CustomBansPlus m = (CustomBansPlus) ClassGetter.getPlugin();

    YamlReport (UUID uuid, String date, String reporterUuid, String report, boolean resolved, String resolverUuid, int reportNum) {
        this.setUuid(uuid);
        this.setReportNum(reportNum);
        this.setDate(date);
        this.setReporterUuid(reporterUuid);
        this.setReport(report);
        this.setResolved(resolved);
        this.setResolverUuid(resolverUuid);
    }

    public String getResolverUuid() {
        return resolverUuid;
    }

    public void setResolverUuid(String resolverUuid) {
        this.resolverUuid = resolverUuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getReportNum() {
        return reportNum;
    }

    public void setReportNum(int reportNum) {
        this.reportNum = reportNum;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getReporterUuid() {
        return reporterUuid;
    }

    public void setReporterUuid(String reporterUuid) {
        this.reporterUuid = reporterUuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static List<YamlReport> getReports(UUID uuid) {
        
        ArrayList<YamlReport> reports = new ArrayList<YamlReport>();

        if (!m.getReportsConfig().isConfigurationSection(uuid.toString())) return null;

        m.getReportsConfig().getConfigurationSection(uuid.toString()).getKeys(false).forEach(num -> {
            reports.add(new YamlReport(
                uuid,
                m.getReportsConfig().getString(uuid.toString() + "." + num + ".date"),
                m.getReportsConfig().getString(uuid.toString() + "." + num + ".reporter"),
                m.getReportsConfig().getString(uuid.toString() + "." + num + ".report"),
                m.getReportsConfig().getBoolean(uuid.toString() + "." + num + ".resolved"),
                m.getReportsConfig().getString(uuid.toString() + "." + num + ".resolver"),
                Integer.parseInt(num)));
        });

        return reports;

    }


    
}
