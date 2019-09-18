# RISMA
Reduced Instructions Set Mips Assembler. There was a Disassembler too, but I lose them. Related  to OrgArg1 class in 17B.

## Suported Instructions
### Type R
* addu
* slt
* sll
* srl
### Type I
* xori
* lui
* addiu
* lw 
* sw
* beq
* sltiu
* ori
* andi

### Type J
* j
* jal
* jr

## Compiling instructions 
```bash
javac *.java
java AsmToHex <asm_file>
```
