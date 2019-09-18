# int fact (int n)
# {
#   if (n<1) return (1);
#   else return (n*fact(n-1);
# }

# MARS example: Fatoral# Author:  
# Function: implementa uma funcao que calcula o fatorial de um numero passado como parametro 
#

        
        .text                   # Add what follows to the text segment of the program
        .globl  main            # Declare the label main to be a global one
main:
    xori $t0, $t1, 2
    xori $sp, $a0, 8
    xori $23, $19, 0
    lui  $a0, 123
    lui  $s2, 13
    lui  $t1, 50
    addu	$s0, $zero, $v0
    addu	$t1, $v0, $s1
    addu	$sp, $sp, $zero
    addiu	$a0, $zero, 3
	addiu	$sp, $sp, -4
	addiu	$sp, $sp, 4
    lw	$a0, 0($sp)
	lw	$t1, 4($v0)
	lw	$ra, -4($sp)
	sw	$ra, 0($t0)
	sw	$sp, 4($t5)
    sw	$a0, -12($zero)
    beq	$t0, $s2, fact
    beq	$v1, $t2, L1
    beq	$zero, $a1, main
    sltiu	$t0, $zero, 1
    sltiu	$s2, $a0, -2
    sltiu	$t2, $sp, 10
    slt $a1, $t1, $sp
    slt $a0, $s2, $zero
    slt $t3, $t0, $t2

    j main
    j L1
    j fact



    ori     $zero, $a0, 12
    ori     $a0, $s2, 2
    ori     $t0, $v1, 5
    andi    $t0, $zero, 12
    andi    $s2, $a0, 666
    andi    $sp, $v1, 0
    sll     $sp,$zero,5
    sll     $a0,$v1,0
    sll     $gp,$t0,8
    srl     $zero,$zero,0
    srl     $a0,$v1,20
    srl     $gp,$sp,0

	jal	fact
	lw	$ra, 0($sp)
	jr	$ra

fact:	addiu	$sp, $sp, -8

	
	sltiu	$t0, $a0, 1	# teste se n<1. Se n>= 1 salta para L1. 		
	beq	$t0, $zero, L1	# Lembrando: se $a0 <1, entao $t0 = 1
	
	addiu	$v0, $zero, 1	# caso n<1, retorna 1
	addiu	$sp, $sp, 8
	jr	$ra
	
L1:	addiu	$a0, $a0, -1	# n-1
	jal 	fact
	

	addiu	$sp, $sp, 8	#ao retornar da chamada recursiva, restaura $a0 (n)
	#mul	$v0, $a0, $v0	#retorna n*fact(n-1)
	jr	$ra
