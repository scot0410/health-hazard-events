//package com.healthhazard.events.model.utils;
//
//import lombok.experimental.UtilityClass;
//
//import java.util.*;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//@UtilityClass
//public class ContentParser {
//    private static final Pattern CITY_STATE_PATTERN = Pattern.compile(
//            "\\b([A-Z][a-zA-Z.-]*(?:\\s+[A-Z][a-zA-Z.-]*)*)\\s*,\\s*([A-Z]{2}|[A-Z][a-zA-Z]+)\\b"
//    );
//
//    private static final Pattern RECALL_TRIGGER = Pattern.compile(
//            "\\s+(?:is|are|voluntarily|expands its.*)?\\s*(?:recalling|issuing a voluntary recall of|voluntarily recalling)\\s+",
//            Pattern.CASE_INSENSITIVE
//    );
//
//   void checker(String test) {
//
//
//       var found_states = Arrays.stream(State.values())
//               .filter(state -> test.contains(state.getAbbreviation())
//                       || test.contains(state.getFullName()))
//               .toList();
//
//       found_states.forEach(state -> {
//           var indexOfAbbrev = test.indexOf(state.getAbbreviation());
//           //find where hte
//           //Revival Animal Health of Orange City, IA is recalling Breeder’s Edge® and Shelter’s Choice® Feline Milk Replacers due to low, levels of potassium, copper, folic acid, choline and/or pyridoxine (vitamin B6).
//           test.substring()
//       }));
//   }
//}
