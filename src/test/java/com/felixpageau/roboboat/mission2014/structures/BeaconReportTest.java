/**
 * 
 */
package com.felixpageau.roboboat.mission2014.structures;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author felixpageau
 *
 */
public class BeaconReportTest {
    Course course = Course.courseA;
    TeamCode team = new TeamCode("AUVSI");
    BuoyColor buoyColor = BuoyColor.black;
    BuoyPosition buoyPosition = new BuoyPosition(Datum.WGS84, new Latitude(40.689247f), new Longitude(-74.044500f));
    

    /**
     * Test method for {@link com.felixpageau.auvsif.roboboat2014.output.BeaconReport#BeaconReport(Course, TeamCode, BuoyColor, BuoyPosition)}.
     */
    @Test(expected = NullPointerException.class)
    public void testBeaconReportBadCourse() {
        new BeaconReport(null, team, buoyColor, buoyPosition);
    }
    @Test(expected = NullPointerException.class)
    public void testBeaconReportBadTeamCode() {
        new BeaconReport(course, null, buoyColor, buoyPosition);
    }
    @Test(expected = NullPointerException.class)
    public void testBeaconReportBadBuoyColor() {
        new BeaconReport(course, team, null, buoyPosition);
    }
    @Test(expected = NullPointerException.class)
    public void testBeaconReportBadPosition() {
        new BeaconReport(course, team, buoyColor, null);
    }
    @Test
    public void testBeaconReport() {
        assertNotNull(new BeaconReport(course, team, buoyColor, buoyPosition));
    }

    /**
     * Test method for marshalling/unmarshalling
     * @throws IOException 
     */
    @Test
    public void testBeaconReportSerialization() throws IOException {
        ObjectMapper om = new ObjectMapper();
        File json = new File("src/test/java/pinger.json");
        BeaconReport report = om.readValue(json, BeaconReport.class);
        assertNotNull(report);
        System.out.println(new String(om.writeValueAsBytes(report)));
        assertArrayEquals(Files.readAllBytes(json.toPath()), om.writeValueAsBytes(report));
    }

}
