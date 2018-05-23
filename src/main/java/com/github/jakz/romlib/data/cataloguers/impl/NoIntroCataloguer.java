package com.github.jakz.romlib.data.cataloguers.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.jakz.romlib.data.cataloguers.GameCataloguer;
import com.github.jakz.romlib.data.cataloguers.LambdaCataloguer;
import com.github.jakz.romlib.data.game.Date;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Language;
import com.github.jakz.romlib.data.game.Location;
import com.github.jakz.romlib.data.game.Version;
import com.github.jakz.romlib.data.game.VideoFormat;
import com.github.jakz.romlib.data.game.attributes.GameAttribute;
import com.github.jakz.romlib.data.platforms.GBC;

public class NoIntroCataloguer implements GameCataloguer
{
  private static final Map<String, Consumer<Game>> mappers;
  private static final List<LambdaCataloguer> lambdas;

  static
  {
    mappers = new HashMap<>();
    mappers.put("World", game -> game.getLocation().add(Location.WORLD));
    mappers.put("Asia", game -> game.getLocation().add(Location.ASIA));
    mappers.put("USA", game -> { 
      game.getLocation().add(Location.USA);
      game.getLanguages().add(Language.ENGLISH);
    });
    mappers.put("Japan", game -> {
      game.getLocation().add(Location.JAPAN);
      game.getLanguages().add(Language.JAPANESE);
    });
    mappers.put("Europe", game -> game.getLocation().add(Location.EUROPE));
    mappers.put("Italy", game -> game.getLocation().add(Location.ITALY));
    mappers.put("France", game -> game.getLocation().add(Location.FRANCE));
    mappers.put("Germany", game -> game.getLocation().add(Location.GERMANY));
    mappers.put("Spain", game -> game.getLocation().add(Location.SPAIN));
    mappers.put("Poland", game -> game.getLocation().add(Location.POLAND));
    mappers.put("Portugal", game -> game.getLocation().add(Location.PORTUGAL));
    mappers.put("Canada", game -> game.getLocation().add(Location.CANADA));
    mappers.put("Korea", game -> { 
      game.getLocation().add(Location.KOREA);
      game.getLanguages().add(Language.KOREAN);
    });
    mappers.put("Sweden", game -> game.getLocation().add(Location.SWEDEN));
    mappers.put("China", game -> game.getLocation().add(Location.CHINA));
    mappers.put("Australia", game -> game.getLocation().add(Location.AUSTRALIA));
    mappers.put("Brazil", game -> game.getLocation().add(Location.BRASIL));
    mappers.put("Netherlands", game -> game.getLocation().add(Location.NETHERLANDS));
    mappers.put("Russia", game -> game.getLocation().add(Location.RUSSIA));
    mappers.put("Norway", game -> game.getLocation().add(Location.NORWAY));
    mappers.put("Croatia", game -> game.getLocation().add(Location.CROATIA));
    mappers.put("Taiwan", game -> game.getLocation().add(Location.TAIWAN));
    mappers.put("Hong Kong", game -> game.getLocation().add(Location.HONG_KONG));
    mappers.put("Greece", game -> game.getLocation().add(Location.GREECE));
    mappers.put("Hungary", game -> game.getLocation().add(Location.HUNGARY));
    mappers.put("Ukraine", game -> game.getLocation().add(Location.UKRAINE));
    mappers.put("Unknown", game -> {}); // maybe a Location.UNKNOWN should be used?

    mappers.put("En", game -> game.getLanguages().add(Language.ENGLISH));
    mappers.put("It", game -> game.getLanguages().add(Language.ITALIAN));
    mappers.put("Fr", game -> game.getLanguages().add(Language.FRENCH));
    mappers.put("De", game -> game.getLanguages().add(Language.GERMAN));
    mappers.put("Es", game -> game.getLanguages().add(Language.SPANISH));
    mappers.put("Ko", game -> game.getLanguages().add(Language.KOREAN));
    mappers.put("Pt", game -> game.getLanguages().add(Language.PORTUGUESE));
    mappers.put("Nl", game -> game.getLanguages().add(Language.DUTCH));
    mappers.put("Ja", game -> game.getLanguages().add(Language.JAPANESE));
    mappers.put("No", game -> game.getLanguages().add(Language.NORWEGIAN));
    mappers.put("Sv", game -> game.getLanguages().add(Language.SWEDISH));
    mappers.put("Fi", game -> game.getLanguages().add(Language.FINNISH));
    mappers.put("Da", game -> game.getLanguages().add(Language.DANISH));
    mappers.put("Ca", game -> game.getLanguages().add(Language.CATALAN));
    mappers.put("Zh", game -> game.getLanguages().add(Language.CHINESE));
    mappers.put("Hr", game -> game.getLanguages().add(Language.CROATIAN));
    mappers.put("Ru", game -> game.getLanguages().add(Language.RUSSIAN));
    mappers.put("Pl", game -> game.getLanguages().add(Language.POLISH));
    mappers.put("Ru", game -> game.getLanguages().add(Language.RUSSIAN));
    mappers.put("El", game -> game.getLanguages().add(Language.GREEK));
    mappers.put("Hu", game -> game.getLanguages().add(Language.HUNGARIAN));
    mappers.put("Tr", game -> game.getLanguages().add(Language.TURKISH));
    mappers.put("Hi", game -> game.getLanguages().add(Language.HINDI));
    mappers.put("Cs", game -> game.getLanguages().add(Language.CZECH));

    /* TODO: verify that settng the version doesn't override another one in case
     * of multiple versions. Actually maybe it would be better to manage version
     * as List<Version>
     */
    mappers.put("Demo", game -> game.setVersion(Version.DEMO));
    mappers.put("Sample", game -> game.setVersion(Version.SAMPLE));
    mappers.put("Proto", game -> game.setVersion(Version.PROTO));
    mappers.put("Promo", game -> game.setVersion(Version.PROMO));
    
    mappers.put("Unl", game -> game.setLicensed(false));
    
    mappers.put("NTSC", game -> game.setVideoFormat(VideoFormat.NTSC));
    mappers.put("PAL", game -> game.setVideoFormat(VideoFormat.PAL));
    
    mappers.put("GB Compatible", game -> game.setCustomAttribute(GBC.Attribute.GB_COMPATIBLE, true));
    mappers.put("SGB Enhanced", game -> game.setCustomAttribute(GBC.Attribute.SGB_ENHANCED, true));
    mappers.put("Rumble Version", game -> game.setCustomAttribute(GBC.Attribute.RUMBLE_VERSION, true));
    
    mappers.put("[b]", game -> game.setAttribute(GameAttribute.BAD_DUMP, true));

    
    /* PSP collections */
    mappers.put("PSP Essentials", game -> game.setAttribute(GameAttribute.COLLECTION, "PSP Essentials"));
    mappers.put("Platinum", game -> game.setAttribute(GameAttribute.COLLECTION, "Platinum"));
    mappers.put("PSP The Best", game -> game.setAttribute(GameAttribute.COLLECTION, "PSP The Best"));

    
    lambdas = new ArrayList<>();
    
    lambdas.add((token,game) -> {
      boolean valid = token.startsWith("Rev ");   
      if (valid)
        game.setVersion(new Version.Revision(token));
      return valid;
    });
    
    lambdas.add((token,game) -> {
      boolean valid = token.startsWith("Beta");   
      if (valid)
        game.setVersion(new Version.Beta(token));
      return valid;
    });
    
    lambdas.add(
      new LambdaCataloguer() {
        private final Pattern versionPattern = Pattern.compile("^v([0-9]+)\\.([0-9]+)(\\.?[a-zA-Z0-9]*)?$");
        
        @Override
        public boolean catalogue(String token, Game game)
        {
          Matcher matcher = versionPattern.matcher(token);
          boolean matched = matcher.find();
          
          if (matched)
          {
            int major = Integer.parseInt(matcher.group(1));
            //int minor = Integer.parseInt(matcher.group(2));
            String suffix = matcher.group(3);
            game.setVersion(new Version.Numbered(major, matcher.group(2), suffix));
          }
          
          return matched;
        }
        
      }
    );
    
    lambdas.add(
      new LambdaCataloguer() {
        private final Pattern datePattern = Pattern.compile("^([1-2][0-9]{3})(?:-(1[0-2]|0?[1-9]))?(?:-([0-3][0-9]))?$");
        
        @Override
        public boolean catalogue(String token, Game game)
        {
          Matcher matcher = datePattern.matcher(token);
          boolean matched = matcher.find();
          
          if (matched)
          {       
            int year = Integer.parseInt(matcher.group(1));
            int month = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : -1;
            int day = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : -1;
            
            Date date = new Date(year, month, day);
              
            //TODO: set attribute
          }
          
          return matched;
        }
      });
        
    
  }
  
  private final Map<String, List<String>> unknownTokens;
  
  public NoIntroCataloguer()
  {
    unknownTokens = new TreeMap<>();
  }
  
  public void catalogue(Game game)
  {
    String title = game.getTitle();

    int firstParen = title.indexOf('(');

    Arrays.stream(title.substring(firstParen).split("\\(|\\)")).filter(s -> !s.isEmpty()).map(s -> s.trim()).forEach(s -> {
      Arrays.stream(s.split(",")).map(t -> t.trim()).filter(t -> !t.isEmpty()).forEach(t -> {
        Consumer<Game> mapper = mappers.get(t);
        
        if (mapper != null)
        {
          mapper.accept(game);
          return;
        }
        
        for (LambdaCataloguer lambda : lambdas)
        {
          if (lambda.catalogue(t, game))
            return;
        }
        
        //if (game.getLocation().isJust(Location.EUROPE) && game.getLanguages().empty())
        //  game.getLanguages().add(Language.ENGLISH_UK);
        
        List<String> games = unknownTokens.computeIfAbsent(t, kk -> new ArrayList<>());
        games.add(game.getTitle());
      }); 
    });    
  }
  
  @Override public void done() { printAddendums(); }
  
  public void printAddendums()
  {    
    unknownTokens.forEach((k,v) -> {
      System.out.println(k+":");
      v.forEach(g -> System.out.println("   "+g));
    });
  }
}