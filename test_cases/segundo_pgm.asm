 .text   
         .globl  main            
         
 main:  #la	$t0,A		# Carrega endereco de A em $t0 - PSEUDO-INSTRUCAO
 	lw	$t0,0($t0)	# Le valor de A para $t0
 	#la	$t1,B		# Carrega endereco de B em $t1 - PSEUDO-INSTRUCAO
 	lw	$t1,0($t1)	# Le valor de B para $t1
 	addu	$t0,$t0,$t1	# $t0 recebe A+B
 
 	#la	$t3,D		# Carrega endereco de D em $t3 - PSEUDO-INSTRUCAO
 	lw	$t3,0($t3)	# Le valor de D para $t3
 	addu	$t0, $t0, $t3	# $t0 <- A+B+D
 	addiu	$t0, $t0, -5	# $t0 <- A+B+D-5
 	
 	##### Opcional ####
 	#la	$t2,C		# Carrega endereco de C em $t2 - PSEUDO-INSTRUCAO
 	sw	$t0,0($t2)	# C recebe A+B
 	#################
 	
 	#la	$t4,F		# Carrega endereco de F em $t4 - PSEUDO-INSTRUCAO
 	lw	$t4,0($t4)	# Le valor de F para $t4
 	addu	$t0, $t0, $t4
 	
 	#la	$t5,E		# Carrega endereco de C em $t2 - PSEUDO-INSTRUCAO
	sw	$t0,0($t5)	# E recebe A+B+D-5+F

 	
 	jr	$ra		# volta para o kernel do simulador
 	
 	
 	.data
 A:	.word	32
 B:	.word	0x05
 C:	.word	0
 D:	.word	3
 E:	.word	0
 F:	.word	1
        
 
 