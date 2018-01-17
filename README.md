# CIL Interpreter
CIL Interpreter is a simple Kotlin app that can interpret basic CIL commands

## Known bugs:
- Closing curve bracket ("}") symbol in ldstr argument may cause exception

## Supported types
- int32
- float64
- string

## Supported commands 
### Math operations:
- add 
- sub
- mul
- div (result is float64 only)

### Transfer:
- beq
- bge
- bgt
- ble
- blt
- br
- brtrue
- brfalse

### Comparing:
- ceq
- cgt
- clt

### Adding values:
- ldc.i4
- ldc.r8
- ldstr

### Local variables:
- stloc
- ldloc

### Partially supported:
- call (WriteLine, ReadLine)
- ret
- conv.r8 (convers int32 to float64)
