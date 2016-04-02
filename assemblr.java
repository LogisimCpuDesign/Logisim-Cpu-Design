import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
public class assemblr {
	String lines[]=new String[1000];
	int lineCounter=0;
	int cc=0;
	public assemblr(String file) throws FileNotFoundException, IOException{
	readFile(file);
	spaceReplacer();
	createBinary();
	binaryToHex();
	writeFile("output");
	}
	public void readFile(String file) throws FileNotFoundException, IOException{
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       
		       lines[lineCounter]=line;
		       lineCounter++;
		    }
		    br.close();
		}
		
	}
	public void writeFile(String file) throws FileNotFoundException, IOException{
		Writer  br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
		for(int i=0;i<lineCounter;i++){
			br.write(lines[i]+" ");
		}
		br.close();
	}
	public void spaceReplacer(){
		for(int i=0;i<lineCounter;i++){
			lines[i]=lines[i].replace(" ", ",");
		}
	}
	public void createBinary(){
		for(cc=0;cc<lineCounter;cc++){
			String splittedLines[]=lines[cc].split(",");
			lines[cc]="";
			String firstItem=splittedLines[0];
			for(int j=0;j<splittedLines.length;j++){
			      splittedLines[j]=findOpCode(splittedLines[j],firstItem,cc);
			      lines[cc]=lines[cc]+splittedLines[j];
			}
			if(lines[cc].length()<16){
				int lt=lines[cc].length();
				for(int j=0;j<16-lt;j++){
					lines[cc]=lines[cc]+"0";
				}
			}
		}
	}
	public String findOpCode(String name,String op,int where){
		switch(name){
		case "R0"  : return "000";
		case "R1"  : return "001";
		case "R2"  : return "010";
		case "R3"  : return "011";
		case "R4"  : return "100";
		case "R5"  : return "101";
		case "R6"  : return "110";
		case "R7"  : return "111";
		case "ADD" : return "0000";
		case "ADDI": return "0001";
		case "AND" : return "0010";
		case "ANDI": return "0011";
		case "CMP" : return "0100";
		case "CMPI": return "0101";
		case "JUMP": return "0110";
		case "LD"  : return "0111";
		case "ST"  : return "1000";
		case "JE"  : return "1001";
		case "JA"  : return "1010";
		case "JB"  : return "1011";
		case "JAE" : return "1100";
		case "JBE" : return "1101";
		default    : return decimalToSignedBinary(name,op,where);
		}
	}
	public String decimalToSignedBinary(String decimal,String op,int where){
		int dec=Integer.parseInt(decimal);
		if(op.equals("ANDI")||op.equals("ADDI")){
			if(dec>31||dec<-32){
				return "fail";
			}
			else{
				decimal=Integer.toBinaryString(dec);
				int lt=decimal.length();
				for(int i=0;i<6-lt;i++){
					if(dec>=0){
						decimal="0"+decimal;
						}
						else{
							decimal="1"+decimal;	
						}
				}
				decimal=decimal.substring(decimal.length()-6);
			}
		}
		else if(op.equals("CMPI")||op.equals("JUMP")||op.equals("ST")||op.equals("LD")
				||op.equals("JE")||op.equals("JA")||op.equals("JB")||op.equals("JAE")
				||op.equals("JBE")){
			if(op.equals("ST")||op.equals("LD")
					||op.equals("JE")||op.equals("JA")||op.equals("JB")||op.equals("JAE")
					||op.equals("JBE")){
				if(dec<0||(lineCounter-dec)<=0){
					return "fail";
				}
			}
			if(op.equals("JUMP")){
				if(dec+where<0){
					return "fail";
				}
			}
				decimal=Integer.toBinaryString(dec);
				int lt=decimal.length();
				for(int i=0;i<9-lt;i++){
					if(dec>=0){
					decimal="0"+decimal;
					}
					else{
						decimal="1"+decimal;	
					}
				}
				
			    if(op.equals("LD")==false&&op.equals("ST")==false&&op.equals("CMPI")==false){
			    	decimal=decimal.substring(decimal.length()-9);
			    	decimal="000"+decimal;
			    }
			
		}
	    return decimal;
	}
	public void binaryToHex(){
		for(int j=0;j<lineCounter;j++){
			String binary = lines[j];
		   lines[j]=Long.toHexString(Long.parseLong(binary,2));
		    lines[j]=String.format("%" + (int)(Math.ceil(binary.length() / 4.0)) + "s", lines[j]).replace(' ', '0');
		    lines[j]=lines[j].toUpperCase();
		}
	}
	public static void main(String[] args) throws FileNotFoundException, IOException{
		assemblr as=new assemblr(args[0]);
	}
}
