import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class T1 {

    //Classe para labels
    private class Label {

        String label;
        Instrucao referencia;

        public Label(String s, Instrucao i) {
            this.label = s;
            this.referencia = i;
        }

        public Instrucao getReferencia(){
            return referencia;
        }

    }

    static int instruCount = 0;
    static int hexCount = 0x00400000;
    boolean hasData = false;

    interface MachineCodavel {
        public int toInt();
    }

    private abstract class Instrucao {
        int codeLine;
        int codeHex;

        public Instrucao() {
            codeLine = instruCount;
            instruCount++;
            codeHex = hexCount;
            hexCount += 4;
        }

        public int getCodeLine(){
            return  codeLine;
        }

        public int getCodeHex(){
            return codeHex;
        }

        //precisa usar long
        public abstract long toLong();

        public abstract String toString();

    }

    //Classe para instrucoes tipo R
    private class InstTipoR extends Instrucao {
        String op;
        String rd;
        String rs;
        String rt;

        InstTipoR(String op, String rd, String rs, String rt) {
            super();
            this.op = op;
            this.rd = rd;
            this.rs = rs;
            this.rt = rt;
        }

        @Override
        public long toLong() {
            long opCode;
            int rdCode;
            int rsCode;
            int rtCode;
            int shamtCode = 0;
            int funcCode = 0;

            rdCode = dollarToInt(rd);
            rsCode = dollarToInt(rs);
            rtCode = dollarToInt(rt);

            opCode = opToOpCode(op);
            if(opCode == 0){
                funcCode = opToFuncCode(op);
                if(funcCode == 0 || funcCode == 2){
                    shamtCode = Integer.valueOf(rt);
                    rtCode = rsCode;
                    //Unused is 0?
                    rsCode = 0;
                }
            }

            //Shifta valores
            {
                opCode = opCode << 26;
                rsCode = rsCode << 21;
                rtCode = rtCode << 16;
                rdCode = rdCode << 11;
                shamtCode = shamtCode << 6;
                //funcCode nao shifta
                //funcCode = funcCode;
            }
            long  machineInt = opCode+rsCode+rtCode+rdCode+shamtCode+funcCode;

            return machineInt;
        }


        @Override
        public String toString(){
            return op + " " + rd + " " + rs + " " + rt;
        }

    }

    //Classe para instrucoes tipo I
    private class InstTipoI extends Instrucao {
        String op;
        String rs;
        String rt;
        String imm;

        InstTipoI(String op, String rs, String rt, String imm) {
            super();
            this.op = op;
            this.rs = rs;
            this.rt = rt;
            this.imm = imm;
        }

        @Override
        public long toLong() {
            long opCode;
            int rsCode;
            int rtCode;
            int immCode;
            int aux;

            opCode = opToOpCode(op);
            rsCode = dollarToInt(rs);
            rtCode = dollarToInt(rt);
            //Zero-extended vs. Sign-Extended
            immCode = immToImmCode(imm);


            switch ((int)opCode){
                case 15: //lui
                    rsCode = rtCode;
                    rtCode = 0;
                    break;
                case 35: //lw
                    rsCode = immToReg(imm);
                    aux = rsCode;
                    rsCode = rtCode;
                    rtCode = aux;
                    immCode = immToOffset(imm);
                break;
                case 43: //sw
                    rsCode = immToReg(imm);
                    aux = rsCode;
                    rsCode = rtCode;
                    rtCode = aux;
                    immCode = immToOffset(imm);
                break;
                case 4:  //beq
                    aux = rsCode;
                    rsCode = rtCode;
                    rtCode = aux;
                    immCode = immToAdress(imm,codeHex);
                break;
            }


            //Shifta valores
            {
                opCode = opCode << 26;
                rsCode = rsCode << 16;
                rtCode = rtCode << 21;
            }
            long machineInt = opCode+rsCode+rtCode+immCode;

            return machineInt;
        }

        @Override
        public String toString(){
            return op + " " + rs + " " + rt + " " + imm;
        }

    }

    //Classe para instrucoes tipo J
    private class InstTipoJ extends Instrucao {
        String op;
        String target;

        InstTipoJ(String op, String target) {
            super();
            this.op = op;
            this.target = target;
        }

        @Override
        public long toLong() {
            long opCode;
            int targetCode;
            long machineInt;

            opCode = opToOpCode(op);

            if(opCode == 0){ //jr

                targetCode = dollarToInt(target);
                targetCode = targetCode << 21;
                int funcCode = 8;

                machineInt = opCode + targetCode + funcCode;
                return  machineInt;
            }

            targetCode = labelAdress(target);


            //Shifta valores
            {
                opCode = opCode << 26;
                targetCode = targetCode >> 2;
            }

            machineInt = opCode + targetCode;
            return machineInt;
        }

        @Override
        public String toString(){
            return op + " " + target;
        }

    }

    //Enum de tipos de instrucao
    public enum Tipo {
        J, I, R
    }

    private Deque<String> tokens;//FIFO para tokens
    private List<String> tipoR;//Lista de instrucoes tipo R
    private List<String> tipoI;//Lista de instrucoes tipo I
    private List<String> tipoI2;//Lista de instrucoes tipoI com 2 argumentos
    private List<String> tipoJ;//Lista de instrucoes tipo J
    private List<Instrucao> instrucoes;//Lista para armazenar instrucoes
    private List<Label> labels;//Lista para armazenar labels


    public T1() {
        tokens = new ArrayDeque<>();
        instrucoes = new ArrayList<>();
        labels = new ArrayList<>();

        tipoR = new ArrayList<>();
        tipoR.add("addu");
        tipoR.add("slt");
        tipoR.add("sll");
        tipoR.add("srl");

        tipoI = new ArrayList<>();
        tipoI.add("xori");
        tipoI.add("lui");
        tipoI.add("addiu");
        tipoI.add("lw");//
        tipoI.add("sw");//
        tipoI.add("beq");//
        tipoI.add("sltiu");
        tipoI.add("ori");
        tipoI.add("andi");

        tipoI2 = new ArrayList<>();
        tipoI2.add("lui");
        tipoI2.add("lw");
        tipoI2.add("sw");

        tipoJ = new ArrayList<>();
        tipoJ.add("j");
        tipoJ.add("jal");
        tipoJ.add("jr");

    }


    //Le o arquivo asm de entrada
    public void readIn(Scanner sc) {

        //Le linhas
        while (sc.hasNext()) {
//            System.out.println(sc.nextLine());
            procLine(sc.nextLine());
        }

    }

    //Processa a linha do arquivo de entrada
    private void procLine(String line) {

        boolean readSubstr = false;
        boolean lendoToken = false;
        int end = 0;
        int start = 0;

        char[] d = line.toCharArray();
        char[] c = new char[d.length + 1];
        System.arraycopy(d, 0, c, 0, d.length);
        c[c.length - 1] = '\n'; //Enjambre :^D


        //Codigo c-like para interpretar os caracteres
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '#') break;

            if (isSequence(c[i])) {
                if (lendoToken) {
                    end = i;
                    readSubstr = true;
                    lendoToken = false;
                }
            } else {
                if (!lendoToken) {
                    start = i;
                    lendoToken = true;
                    readSubstr = false;
                }
            }

            if (readSubstr) {
                readSubstr = false;
                String token = line.substring(start, end);
//                System.out.println(token);
                //Empilha os tokens
                tokens.offer(token);
            }
        }
    }

    //Função para excluir o que não é token
    private boolean isSequence(char c) {
        if (c == ' ') return true;
        if (c == ',') return true;
        if (c == '\t') return true;
        if (c == '\n') return true;
        return false;
    }

    //Cria instrucoes a partir dos tokens
    public void createInstructions() {

        if(tokens.contains(".data")) hasData = true;
        dropTokens();

        String tk;
        String label;

        while (!tokens.isEmpty()) {
            tk = tokens.poll();

            //Verifica se tem label
            if (tk.contains(":")) {
                label = tk;
                tk = tokens.poll();

                //Monta Instrucao
                assembleInstruction(tk);

                //Cria label
                labels.add(new Label(label.substring(0, label.indexOf(':')), instrucoes.get(instrucoes.size() - 1)));
            } else {
                //Monta Instrucao
                assembleInstruction(tk);
            }
        }
    }

    //Elimina todos os tokens até chegar em main:
    private void dropTokens() {
        if (tokens.isEmpty()) System.out.println("ERRO, SEM DADOS DE ENTRADA");
        String tk1;
        while (!tokens.isEmpty()) {
            tk1 = tokens.poll();
            if (tk1.equals("main:")) {
                tokens.offerFirst(tk1);
                break;
            }
        }
        if(!hasData) return;

        while(!tokens.isEmpty()){
            tk1 = tokens.pollLast();
            if(tk1.equals(".data")){
                return;
            }
        }
        System.out.println("ERRO, ARQUIVO SEM MAIN:");
    }

    //Define qual o tipo de isntrucao a ser montada
    private void assembleInstruction(String tk) {
        Tipo t = selectType(tk);

        switch (t) {
            case R:
                assembleTypeR(tk);
                break;
            case I:
                if (isTipeI2(tk)) {
                    assembleTypeI2(tk);
                } else {
                    assembleTypeI3(tk);
                }
                break;
            case J:
                assembleTypeJ(tk);
                break;

        }
    }

    //Retorna true se é instrução tipo I de 2 argumentos
    private boolean isTipeI2(String tk) {
        if (tipoI2.contains(tk)) return true;
        return false;
    }

    //Retorna o tipo de um token-instrução
    private Tipo selectType(String tk) {
        if (tipoR.contains(tk)) return Tipo.R;
        if (tipoJ.contains(tk)) return Tipo.J;
        if (tipoI.contains(tk)) return Tipo.I;
        System.out.println("ERROR, INSTRUÇÃO NÃO SUPORTADA + " + tk);
        return null;
    }

    //Monta instrução tipo R
    private void assembleTypeR(String op) {
        String rd = tokens.poll();
        String rs = tokens.poll();
        String rt = tokens.poll();

        instrucoes.add(new InstTipoR(op, rd, rs, rt));

    }

    //Monta instrução tipo I de 3 argumentos
    private void assembleTypeI2(String op) {
        String rs = "";
        String rt = tokens.poll();
        String imm = tokens.poll();

        instrucoes.add(new InstTipoI(op, rs, rt, imm));
    }

    //Monta instrução tipo I de 3 argumentos
    private void assembleTypeI3(String op) {
        String rs = tokens.poll();
        String rt = tokens.poll();
        String imm = tokens.poll();

        instrucoes.add(new InstTipoI(op, rs, rt, imm));
    }

    //Monta instrução tipo J
    private void assembleTypeJ(String op) {
        String target = tokens.poll();

        instrucoes.add(new InstTipoJ(op, target));
    }

    //Cria o arquivo de saída
    public void generateOutPut(String fileName) {

        fileName = fileName.substring(0,fileName.lastIndexOf("."));
        fileName = fileName.concat(".hex");

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(fileName, "UTF-8");

            //Percorre instruçoes
            for (Instrucao i : instrucoes) {
//            System.out.println(i.getCodeLine() + ": "+i.toLong());
                System.out.println(i.getCodeLine() + ": "+i.toString() + ": " + i.toLong() + "-> " + String.format("0x%08X", i.toLong()));
                writer.println(String.format("0x%08X", i.toLong()));
            }

            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    //Metodo para converter registrador em seu int
    private int dollarToInt(String s) {

        //TODO: Tratar os $0 - $31
        switch (s) {
            case "$zero":
                return 0;
            case "$at":
                return 1;
            case "$v0":
                return 2;
            case "$v1":
                return 3;
            case "$a0":
                return 4;
            case "$a1":
                return 5;
            case "$a2":
                return 6;
            case "$a3":
                return 7;
            case "$t0":
                return 8;
            case "$t1":
                return 9;
            case "$t2":
                return 10;
            case "$t3":
                return 11;
            case "$t4":
                return 12;
            case "$t5":
                return 13;
            case "$t6":
                return 14;
            case "$t7":
                return 15;
            case "$s0":
                return 16;
            case "$s1":
                return 17;
            case "$s2":
                return 18;
            case "$s3":
                return 19;
            case "$s4":
                return 20;
            case "$s5":
                return 21;
            case "$s6":
                return 22;
            case "$s7":
                return 23;
            case "$t8":
                return 24;
            case "$t9":
                return 25;
            case "$k0":
                return 26;
            case "$k1":
                return 27;
            case "$gp":
                return 28;
            case "$sp":
                return 29;
            case "$fp":
                return 30;
            case "$ra":
                return 31;
        }
        return -1;
    }

    //Metodo para converter opCode em int
    private int opToOpCode(String s) {

        switch (s) {
            case "xori":
                return 14;
            case "lui":
                return 15;
            case "addu":
                return 0;
            case "addiu":
                return 9;
            case "lw":
                return 35;
            case "sw":
                return 43;
            case "beq":
                return 4;
            case "sltiu":
                return 11;
            case "slt":
                return 0;
            case "j":
                return 2;
            case "jal":
                return 3;
            case "jr":
                return 0;
            case "ori":
                return 13;
            case "andi":
                return 12;
            case "sll":
                return 0;
            case "srl":
                return 0;
            }
        return -1;
    }

    //Metodo para pegar codigo de funcao recebendo opCode
    private int opToFuncCode(String s) {

        switch (s) {
            case "addu":
                return 33;
            case "slt":
                return 42;
            case "jr":
                return 8;
            case "sll":
                return 0;
            case "srl":
                return 2;
            }
        return -1;
    }

    private int immToReg(String addrs){
        addrs = addrs.substring(addrs.indexOf('$'),addrs.indexOf(')'));
        return dollarToInt(addrs);
    }

    private int immToOffset(String addrs){
        addrs = addrs.substring(0,addrs.indexOf('('));
        return signExtended(Integer.valueOf(addrs));
    }

    private int immToAdress(String addrs,int codHex){
        for (Label l:   labels) {
            if(addrs.equals(l.label)){
//                return l.getReferencia().getCodeHex() - codHex;
                return ((l.getReferencia().getCodeHex() - codHex)/4 )-1;
            }
        }
        System.out.println("ERRO, LABEL INEXISTENTE");
        return -1;
    }

    private int labelAdress(String addrs){
        for (Label l:   labels) {
            if(addrs.equals(l.label)){
                return l.getReferencia().getCodeHex();
            }
        }
        System.out.println("ERRO, LABEL INEXISTENTE");
        return -1;
    }

    private int immToImmCode(String s){
        //TODO: Tratar hexadecimal?
        int i = 0;
        try{
            i = Integer.valueOf(s);
            i = signExtended(i);
        }catch (Exception e){

        }
        return i;
    }

    //Retorna o valor Sign-Extended de um int 16bits
    private int signExtended(int i){
        return 0x0000FFFF & i;
    }

}
