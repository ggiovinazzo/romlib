package com.github.jakz.romlib.parsers;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.set.DatFormat;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.Feature;
import com.github.jakz.romlib.data.set.GameList;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.parser.SimpleParser;
import com.pixbits.lib.parser.SimpleTreeBuilder;

public class ClrMameProParser implements DataSupplier
{
  RomSize.Set sizeSet;
  GameSet set;
  List<Rom> roms;
  List<Game> games;
  
  String gameName;
  
  String romName;
  long crc;
  RomSize size;
  byte[] md5;
  byte[] sha1;
  
  private final HexBinaryAdapter hexConverter = new HexBinaryAdapter();
  
  @Override public DatFormat getFormat() { return DatFormat.of("clr-mame-pro", "cm", "dat"); }

  @Override public DataSupplier.Data load(GameSet set)
  {
    this.set = set;
    load(set.datPath());

    return new DataSupplier.Data(new GameList(games, sizeSet));
  }
  
  public void load(Path datFile)
  {
    try (InputStream fis = new BufferedInputStream(Files.newInputStream(datFile)))
    {
      //StringReader sr = new StringReader(input);  new ByteArrayInputStream(input.getBytes("UTF-8"))
      SimpleParser parser = new SimpleParser(fis);
      parser.addSingle('(', ')').addQuote('\"').addWhiteSpace(' ', '\t', '\r', '\n');

      SimpleTreeBuilder builder = new SimpleTreeBuilder(parser, this::pair, this::scope);
      builder.setScope("(", ")");
      
      parser.parse();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  boolean started = false;
  boolean insideRom = false;

  public void pair(String k, String v)
  {
    if (!started)
      return;
    
    if (k.equals("name"))
    {
      if (!insideRom)
        gameName = v;
      else
        romName = v;
    }
    else if (k.equals("size"))
      size = sizeSet.forBytes(Long.parseLong(v));
    else if (k.equals("crc"))
      crc = Long.parseLong(v, 16);
    else if (k.equals("sha1"))
      md5 = hexConverter.unmarshal(v);
    else if (k.equals("md5"))
      sha1 = hexConverter.unmarshal(v);
  }
  
  public void scope(String k, boolean isEnd)
  {   
    if (k.equals("rom"))
    {
      insideRom = !isEnd;
      if (isEnd)
        roms.add(new Rom(romName, size, crc, md5, sha1));
    }
    else if (k.equals("game"))
    {
      if (isEnd)
      {
        Game game = new Game(set, roms.toArray(new Rom[roms.size()]));
        game.setTitle(gameName);
        games.add(game);
      }
      else
        roms = new ArrayList<>();
    }
    else if (isEnd && k.equals("clrmamepro"))
    {
      started = true;
      games = new ArrayList<>();
      roms = new ArrayList<>();
      sizeSet = set.hasFeature(Feature.FINITE_SIZE_SET) ? new RomSize.RealSet() : new RomSize.NullSet();
    }
  }
}