package com.felixpageau.roboboat.mission;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.felixpageau.roboboat.mission.nmea.EnumField;
import com.felixpageau.roboboat.mission.nmea.Field;
import com.felixpageau.roboboat.mission.nmea.RegexField;
import com.felixpageau.roboboat.mission.nmea.SentenceDefinition;
import com.felixpageau.roboboat.mission.nmea.SentenceRegistry;
import com.felixpageau.roboboat.mission.structures.BuoyColor;
import com.felixpageau.roboboat.mission.structures.Challenge;
import com.felixpageau.roboboat.mission.structures.Course;
import com.felixpageau.roboboat.mission.structures.Symbol;
import com.felixpageau.roboboat.mission.structures.SymbolColor;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class SentenceRegistryFactory {
  public static SentenceRegistry createNMEASentenceRegistry() {
    Field course = new EnumField("Course", Arrays.stream(Course.values()).map(c -> c.name()).collect(Collectors.toSet()));
    Field teamCode = new RegexField("Teamcode", "[a-zA-Z]{2,5}");
    Field timestamp = new RegexField("Timestamp", "[0-9]{14}");
    Field status = new EnumField("Status", ImmutableSet.of("true", "false"));
    Field entrance = new EnumField("Entrance", ImmutableSet.of("1", "2", "3"));
    Field exit = new EnumField("Exit", ImmutableSet.of("X", "Y", "Z"));
    Field symbol = new EnumField("Symbol", Arrays.stream(Symbol.values()).map(s -> s.name()).collect(Collectors.toSet()));
    Field symbolColor = new EnumField("Color", Arrays.stream(SymbolColor.values()).map(s -> s.name()).collect(Collectors.toSet()));
    Field buoyColor = new EnumField("BuoyColor", Arrays.stream(BuoyColor.values()).map(s -> s.name()).collect(Collectors.toSet()));
    Field buoyFrequency = new RegexField("Frequency", "[0-9]{2}");
    Field shape = new RegexField("Shape", "[0-9a-fA-F]");
    Field imageId = new RegexField("ImageID", "[a-zA-Z0-9\\-]+");
    Field challenge = new EnumField("Challenge", Arrays.stream(Challenge.values()).map(c -> c.toString()).collect(Collectors.toSet()));
    Field latitude = new RegexField("Latitude", "\\-?[0-9]{1,2}\\.[0-9]{6,10}");
    Field longitude = new RegexField("Longitude", "\\-?[0-9]{1,3}\\.[0-9]{6,10}");

    List<SentenceDefinition> sentences = ImmutableList.of(SentenceDefinition.create("SV", "OBS", "Request Obstacle Avoidance code",
        ImmutableList.of(course, teamCode), "$SVOBS,courseA,AUVSI*5F"), SentenceDefinition.create("TD", "OBS", "Provide Obstacle Avoidance code",
        ImmutableList.of(timestamp, entrance, exit), "$TDOBS,20150306061030,2,X*0F"), SentenceDefinition.create("SV", "DOC",
        "Request Automated Docking sequence", ImmutableList.of(course, teamCode), "$SVDOC,courseA,AUVSI*49"), SentenceDefinition.create("TD", "DOC",
        "Provide Automated Docking sequence", ImmutableList.of(symbol, symbolColor, symbol, symbolColor), "$TDDOC,cruciform,red,triangle,blue*43"),
        SentenceDefinition.create("SV", "PIN", "Report active pinger", ImmutableList.of(course, teamCode, buoyColor, buoyFrequency, buoyColor, buoyFrequency),
            "$SVPIN,courseA,AUVSI,red,24,black,36*41"), SentenceDefinition.create("TD", "PIN", "Response of active pinger report",
            ImmutableList.of(timestamp, status), "$TDPIN,20150306061030,true*56"), SentenceDefinition.create("SV", "UAV", "Report shape for interop challenge",
            ImmutableList.of(course, teamCode, shape, imageId), "$SVUAV,courseA,AUVSI,8,a4aa8224-07f2-4b57-a03a-c8887c2505c7*7F"), SentenceDefinition.create(
            "TD", "UAV", "Response of interop shape report", ImmutableList.of(timestamp, status), "$TDUAV,20150306061030,true*43"), SentenceDefinition.create(
            "SV", "HRT", "Report heartbeat", ImmutableList.of(course, teamCode, timestamp, challenge, latitude, longitude),
            "$SVHRT,courseA,AUVSI,20150306061030,gates,40.689249,-74.044500*0B"), SentenceDefinition.create("TD", "HRT", "Response to heartbeat",
            ImmutableList.of(timestamp, status), "$TDHRT,20150306061030,true*4F"), SentenceDefinition.create("SV", "STR", "Request start of run",
            ImmutableList.of(course, teamCode), "$SVSTR,courseA,AUVSI*54"), SentenceDefinition.create("TD", "STR", "Response of start of run",
            ImmutableList.of(status), "$TDSTR,true*7F"), SentenceDefinition.create("SV", "END", "Request of end of run", ImmutableList.of(course, teamCode),
            "$SVEND,courseA,AUVSI*4E"), SentenceDefinition.create("TD", "END", "Response of start of run", ImmutableList.of(status), "$TDEND,true*65"));
    return new SentenceRegistry(sentences);
  }
}
