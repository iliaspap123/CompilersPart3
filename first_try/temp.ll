declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)
@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
define void @print_int(i32 %i) {
%_str = bitcast [4 x i8]* @_cint to i8*
call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
ret void
}
define void @throw_oob() {
  
%_str = bitcast [15 x i8]* @_cOOB to i8*
call i32 (i8*, ...) @printf(i8* %_str)
call void @exit(i32 1)
ret void
}
@.LinkedList_vtable = global [0 x i8*] []
define i32 @main() {
	%_0 = call i8* @calloc(i32 1, i32 8)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [1 x i8*], [1 x i8*]* @.LL_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	; LL.Start: 0
	%_3 = bitcast i8* %_0 to i8***
	%_4 = load i8**, i8*** %_3
	%_5 = getelementptr i8*, i8** %_4, i32 0
	%_6 = load i8*, i8** %_5
	%_7 = bitcast i8* %_6 to i32 (i8*)*
	%_8 = call i32 %_7(i8* %_0)
	call void (i32) @print_int(i32 %_8)
	ret i32 0
}
@.Element_vtable = global [6 x i8*] [
				i8* bitcast (i1(i8*,i32,i32,i1)* @Element.Init to i8*)
,				i8* bitcast (i32(i8*)* @Element.GetAge to i8*)
,				i8* bitcast (i32(i8*)* @Element.GetSalary to i8*)
,				i8* bitcast (i1(i8*)* @Element.GetMarried to i8*)
,				i8* bitcast (i1(i8*,i8*)* @Element.Equal to i8*)
,				i8* bitcast (i1(i8*,i32,i32)* @Element.Compare to i8*)
				]
define i1 @Element.Init(i8* %this, i32 %.v_Age, i32 %.v_Salary, i1 %.v_Married) {
	%v_Age = alloca i32
	store i32 %.v_Age, i32* %v_Age
	%v_Salary = alloca i32
	store i32 %.v_Salary, i32* %v_Salary
	%v_Married = alloca i1
	store i1 %.v_Married, i1* %v_Married

	%_9 = getelementptr i8, i8* %this, i32 8
	%_10 = bitcast i8* %_9 to i32*
	%_11 = load i32, i32* %v_Age
	store i32 %_11, i32* %_10
	%_12 = getelementptr i8, i8* %this, i32 12
	%_13 = bitcast i8* %_12 to i32*
	%_14 = load i32, i32* %v_Salary
	store i32 %_14, i32* %_13
	%_15 = getelementptr i8, i8* %this, i32 16
	%_16 = bitcast i8* %_15 to i1*
	%_17 = load i1, i1* %v_Married
	store i1 %_17, i1* %_16
	ret i1 1
}
define i32 @Element.GetAge(i8* %this) {

	%_18 = getelementptr i8, i8* %this, i32 8
	%_19 = bitcast i8* %_18 to i32*
	%_20 = load i32, i32* %_19
	ret i32 %_20
}
define i32 @Element.GetSalary(i8* %this) {

	%_21 = getelementptr i8, i8* %this, i32 12
	%_22 = bitcast i8* %_21 to i32*
	%_23 = load i32, i32* %_22
	ret i32 %_23
}
define i1 @Element.GetMarried(i8* %this) {

	%_24 = getelementptr i8, i8* %this, i32 16
	%_25 = bitcast i8* %_24 to i1*
	%_26 = load i1, i1* %_25
	ret i1 %_26
}
define i1 @Element.Equal(i8* %this, i8* %.other) {
	%other = alloca i8*
	store i8* %.other, i8** %other

	%ret_val = alloca i1
	%aux01 = alloca i32
	%aux02 = alloca i32
	%nt = alloca i32
	store i1 1, i1* %ret_val
	%_27 = load i8*, i8** %other
	; Element.GetAge: 8
	%_28 = bitcast i8* %_27 to i8***
	%_29 = load i8**, i8*** %_28
	%_30 = getelementptr i8*, i8** %_29, i32 8
	%_31 = load i8*, i8** %_30
	%_32 = bitcast i8* %_31 to i32 (i8*)*
	%_33 = call i32 %_32(i8* %_27)
	store i32 %_33, i32* %aux01
	; Element.Compare: 40
	%_34 = bitcast i8* %this to i8***
	%_35 = load i8**, i8*** %_34
	%_36 = getelementptr i8*, i8** %_35, i32 40
	%_37 = load i8*, i8** %_36
	%_38 = bitcast i8* %_37 to i1 (i8*,i32,i32)*
	%_39 = load i32, i32* %aux01
	%_40 = getelementptr i8, i8* %this, i32 8
	%_41 = bitcast i8* %_40 to i32*
	%_42 = load i32, i32* %_41
	%_43 = call i1 %_38(i8* %this, i32 %_39, i32 %_42)
%_44 = xor i1 1, %_43
	br i1 %_44, label %if0, label %else0
if0:
	store i1 0, i1* %ret_val

	br label %end0
else0:
	%_45 = load i8*, i8** %other
	; Element.GetSalary: 16
	%_46 = bitcast i8* %_45 to i8***
	%_47 = load i8**, i8*** %_46
	%_48 = getelementptr i8*, i8** %_47, i32 16
	%_49 = load i8*, i8** %_48
	%_50 = bitcast i8* %_49 to i32 (i8*)*
	%_51 = call i32 %_50(i8* %_45)
	store i32 %_51, i32* %aux02
	; Element.Compare: 40
	%_52 = bitcast i8* %this to i8***
	%_53 = load i8**, i8*** %_52
	%_54 = getelementptr i8*, i8** %_53, i32 40
	%_55 = load i8*, i8** %_54
	%_56 = bitcast i8* %_55 to i1 (i8*,i32,i32)*
	%_57 = load i32, i32* %aux02
	%_58 = getelementptr i8, i8* %this, i32 12
	%_59 = bitcast i8* %_58 to i32*
	%_60 = load i32, i32* %_59
	%_61 = call i1 %_56(i8* %this, i32 %_57, i32 %_60)
%_62 = xor i1 1, %_61
	br i1 %_62, label %if0, label %else0
if0:
	store i1 0, i1* %ret_val

	br label %end0
else0:
	%_63 = getelementptr i8, i8* %this, i32 16
	%_64 = bitcast i8* %_63 to i1*
	%_65 = load i1, i1* %_64
	br i1 %_65, label %if0, label %else0
if0:
	%_66 = load i8*, i8** %other
	; Element.GetMarried: 24
	%_67 = bitcast i8* %_66 to i8***
	%_68 = load i8**, i8*** %_67
	%_69 = getelementptr i8*, i8** %_68, i32 24
	%_70 = load i8*, i8** %_69
	%_71 = bitcast i8* %_70 to i1 (i8*)*
	%_72 = call i1 %_71(i8* %_66)
%_73 = xor i1 1, %_72
	br i1 %_73, label %if0, label %else0
if0:
	store i1 0, i1* %ret_val

	br label %end0
else0:
	store i32 0, i32* %nt

	br label %end0

end0:

	br label %end1
else1:
	%_74 = load i8*, i8** %other
	; Element.GetMarried: 24
	%_75 = bitcast i8* %_74 to i8***
	%_76 = load i8**, i8*** %_75
	%_77 = getelementptr i8*, i8** %_76, i32 24
	%_78 = load i8*, i8** %_77
	%_79 = bitcast i8* %_78 to i1 (i8*)*
	%_80 = call i1 %_79(i8* %_74)
	br i1 %_80, label %if1, label %else1
if1:
	store i1 0, i1* %ret_val

	br label %end1
else1:
	store i32 0, i32* %nt

	br label %end1

end1:

	br label %end2

end2:

	br label %end3

end3:

	br label %end4

end4:
	%_81 = load i1, i1* %ret_val
	ret i1 %_81
}
define i1 @Element.Compare(i8* %this, i32 %.num1, i32 %.num2) {
	%num1 = alloca i32
	store i32 %.num1, i32* %num1
	%num2 = alloca i32
	store i32 %.num2, i32* %num2

	%retval = alloca i1
	%aux02 = alloca i32
	store i1 0, i1* %retval
	%_82 = load i32, i32* %num2
	%_83 = add i32 %_82, 1
	store i32 %_83, i32* %aux02
	%_84 = load i32, i32* %num1
	%_85 = load i32, i32* %num2
	%_86 = icmp slt i32 %_84, %_85
	br i1 %_86, label %if5, label %else5
if5:
	store i1 0, i1* %retval

	br label %end5
else5:
	%_87 = load i32, i32* %num1
	%_88 = load i32, i32* %aux02
	%_89 = icmp slt i32 %_87, %_88
%_90 = xor i1 1, %_89
	br i1 %_90, label %if5, label %else5
if5:
	store i1 0, i1* %retval

	br label %end5
else5:
	store i1 1, i1* %retval

	br label %end5

end5:

	br label %end6

end6:
	%_91 = load i1, i1* %retval
	ret i1 %_91
}
@.List_vtable = global [10 x i8*] [
				i8* bitcast (i1(i8*)* @List.Init to i8*)
,				i8* bitcast (i1(i8*,i8*,i8*,i1)* @List.InitNew to i8*)
,				i8* bitcast (i8*(i8*,i8*)* @List.Insert to i8*)
,				i8* bitcast (i1(i8*,i8*)* @List.SetNext to i8*)
,				i8* bitcast (i8*(i8*,i8*)* @List.Delete to i8*)
,				i8* bitcast (i32(i8*,i8*)* @List.Search to i8*)
,				i8* bitcast (i1(i8*)* @List.GetEnd to i8*)
,				i8* bitcast (i8*(i8*)* @List.GetElem to i8*)
,				i8* bitcast (i8*(i8*)* @List.GetNext to i8*)
,				i8* bitcast (i1(i8*)* @List.Print to i8*)
				]
define i1 @List.Init(i8* %this) {

	%_92 = getelementptr i8, i8* %this, i32 24
	%_93 = bitcast i8* %_92 to i1*
	store i1 1, i1* %_93
	ret i1 1
}
define i1 @List.InitNew(i8* %this, i8* %.v_elem, i8* %.v_next, i1 %.v_end) {
	%v_elem = alloca i8*
	store i8* %.v_elem, i8** %v_elem
	%v_next = alloca i8*
	store i8* %.v_next, i8** %v_next
	%v_end = alloca i1
	store i1 %.v_end, i1* %v_end

	%_94 = getelementptr i8, i8* %this, i32 24
	%_95 = bitcast i8* %_94 to i1*
	%_96 = load i1, i1* %v_end
	store i1 %_96, i1* %_95
	%_97 = getelementptr i8, i8* %this, i32 8
	%_98 = bitcast i8* %_97 to i8**
	%_99 = load i8*, i8** %v_elem
	store i8* %_99, i8** %_98
	%_100 = getelementptr i8, i8* %this, i32 16
	%_101 = bitcast i8* %_100 to i8**
	%_102 = load i8*, i8** %v_next
	store i8* %_102, i8** %_101
	ret i1 1
}
define i8* @List.Insert(i8* %this, i8* %.new_elem) {
	%new_elem = alloca i8*
	store i8* %.new_elem, i8** %new_elem

	%ret_val = alloca i1
	%aux03 = alloca i8*
	%aux02 = alloca i8*
	store i8* %this, i8** %aux03
	%_103 = call i8* @calloc(i32 1, i32 25)
	%_104 = bitcast i8* %_103 to i8***
	%_105 = getelementptr [10 x i8*], [10 x i8*]* @.List_vtable, i32 0, i32 0
	store i8** %_105, i8*** %_104
	store i8* %_103, i8** %aux02
	%_106 = load i8*, i8** %aux02
	; List.InitNew: 8
	%_107 = bitcast i8* %_106 to i8***
	%_108 = load i8**, i8*** %_107
	%_109 = getelementptr i8*, i8** %_108, i32 8
	%_110 = load i8*, i8** %_109
	%_111 = bitcast i8* %_110 to i1 (i8*,i8*,i8*,i1)*
	%_112 = load i8*, i8** %new_elem
	%_113 = load i8*, i8** %aux03
	%_114 = call i1 %_111(i8* %_106, i8* %_112, i8* %_113, i1 0)
	store i1 %_114, i1* %ret_val
	%_115 = load i8*, i8** %aux02
	ret i8* %_115
}
define i1 @List.SetNext(i8* %this, i8* %.v_next) {
	%v_next = alloca i8*
	store i8* %.v_next, i8** %v_next

	%_116 = getelementptr i8, i8* %this, i32 16
	%_117 = bitcast i8* %_116 to i8**
	%_118 = load i8*, i8** %v_next
	store i8* %_118, i8** %_117
	ret i1 1
}
define i8* @List.Delete(i8* %this, i8* %.e) {
	%e = alloca i8*
	store i8* %.e, i8** %e

	%my_head = alloca i8*
	%ret_val = alloca i1
	%aux05 = alloca i1
	%aux01 = alloca i8*
	%prev = alloca i8*
	%var_end = alloca i1
	%var_elem = alloca i8*
	%aux04 = alloca i32
	%nt = alloca i32
	store i8* %this, i8** %my_head
	store i1 0, i1* %ret_val
	%_119 = sub i32 0, 1
	store i32 %_119, i32* %aux04
	store i8* %this, i8** %aux01
	store i8* %this, i8** %prev
	%_120 = getelementptr i8, i8* %this, i32 24
	%_121 = bitcast i8* %_120 to i1*
	%_122 = load i1, i1* %_121
	store i1 %_122, i1* %var_end
	%_123 = getelementptr i8, i8* %this, i32 8
	%_124 = bitcast i8* %_123 to i8**
	%_125 = load i8*, i8** %_124
	store i8* %_125, i8** %var_elem
	br label %loopInit0
loopInit0:
	%_126 = load i1, i1* %var_end
%_127 = xor i1 1, %_126
	br label %andStart7
	andStart7:
	br i1 %_127, label %andclause7, label %andclause9
andclause7:
	%_128 = load i1, i1* %ret_val
%_129 = xor i1 1, %_128
	br label %andclause8
andclause8:
	br label %andclause9
andclause9:
%_130 = phi i1 [ 0,%andStart7], [ %_129, %andclause9]
	br i1 %_130, label %loopStart0, label %loopEnd0
loopStart0:
	%_131 = load i8*, i8** %e
	; Element.Equal: 32
	%_132 = bitcast i8* %_131 to i8***
	%_133 = load i8**, i8*** %_132
	%_134 = getelementptr i8*, i8** %_133, i32 32
	%_135 = load i8*, i8** %_134
	%_136 = bitcast i8* %_135 to i1 (i8*,i8*)*
	%_137 = load i8*, i8** %var_elem
	%_138 = call i1 %_136(i8* %_131, i8* %_137)
	br i1 %_138, label %if7, label %else7
if7:
	store i1 1, i1* %ret_val
	%_139 = load i32, i32* %aux04
	%_140 = icmp slt i32 %_139, 0
	br i1 %_140, label %if7, label %else7
if7:
	%_141 = load i8*, i8** %aux01
	; List.GetNext: 64
	%_142 = bitcast i8* %_141 to i8***
	%_143 = load i8**, i8*** %_142
	%_144 = getelementptr i8*, i8** %_143, i32 64
	%_145 = load i8*, i8** %_144
	%_146 = bitcast i8* %_145 to i8* (i8*)*
	%_147 = call i8* %_146(i8* %_141)
	store i8* %_147, i8** %my_head

	br label %end7
else7:
	%_148 = sub i32 0, 555
	call void (i32) @print_int(i32 %_148)
	%_149 = load i8*, i8** %prev
	; List.SetNext: 24
	%_150 = bitcast i8* %_149 to i8***
	%_151 = load i8**, i8*** %_150
	%_152 = getelementptr i8*, i8** %_151, i32 24
	%_153 = load i8*, i8** %_152
	%_154 = bitcast i8* %_153 to i1 (i8*,i8*)*
	%_155 = load i8*, i8** %aux01
	; List.GetNext: 64
	%_156 = bitcast i8* %_155 to i8***
	%_157 = load i8**, i8*** %_156
	%_158 = getelementptr i8*, i8** %_157, i32 64
	%_159 = load i8*, i8** %_158
	%_160 = bitcast i8* %_159 to i8* (i8*)*
	%_161 = call i8* %_160(i8* %_155)
	%_162 = call i1 %_154(i8* %_149, i8* %_161)
	store i1 %_162, i1* %aux05
	%_163 = sub i32 0, 555
	call void (i32) @print_int(i32 %_163)

	br label %end7

end7:

	br label %end8
else8:
	store i32 0, i32* %nt

	br label %end8

end8:
	%_164 = load i1, i1* %ret_val
%_165 = xor i1 1, %_164
	br i1 %_165, label %if9, label %else9
if9:
	%_166 = load i8*, i8** %aux01
	store i8* %_166, i8** %prev
	%_167 = load i8*, i8** %aux01
	; List.GetNext: 64
	%_168 = bitcast i8* %_167 to i8***
	%_169 = load i8**, i8*** %_168
	%_170 = getelementptr i8*, i8** %_169, i32 64
	%_171 = load i8*, i8** %_170
	%_172 = bitcast i8* %_171 to i8* (i8*)*
	%_173 = call i8* %_172(i8* %_167)
	store i8* %_173, i8** %aux01
	%_174 = load i8*, i8** %aux01
	; List.GetEnd: 48
	%_175 = bitcast i8* %_174 to i8***
	%_176 = load i8**, i8*** %_175
	%_177 = getelementptr i8*, i8** %_176, i32 48
	%_178 = load i8*, i8** %_177
	%_179 = bitcast i8* %_178 to i1 (i8*)*
	%_180 = call i1 %_179(i8* %_174)
	store i1 %_180, i1* %var_end
	%_181 = load i8*, i8** %aux01
	; List.GetElem: 56
	%_182 = bitcast i8* %_181 to i8***
	%_183 = load i8**, i8*** %_182
	%_184 = getelementptr i8*, i8** %_183, i32 56
	%_185 = load i8*, i8** %_184
	%_186 = bitcast i8* %_185 to i8* (i8*)*
	%_187 = call i8* %_186(i8* %_181)
	store i8* %_187, i8** %var_elem
	store i32 1, i32* %aux04

	br label %end9
else9:
	store i32 0, i32* %nt

	br label %end9

end9:
	br label %loopInit0
loopEnd0:
	%_188 = load i8*, i8** %my_head
	ret i8* %_188
}
define i32 @List.Search(i8* %this, i8* %.e) {
	%e = alloca i8*
	store i8* %.e, i8** %e

	%int_ret_val = alloca i32
	%aux01 = alloca i8*
	%var_elem = alloca i8*
	%var_end = alloca i1
	%nt = alloca i32
	store i32 0, i32* %int_ret_val
	store i8* %this, i8** %aux01
	%_189 = getelementptr i8, i8* %this, i32 24
	%_190 = bitcast i8* %_189 to i1*
	%_191 = load i1, i1* %_190
	store i1 %_191, i1* %var_end
	%_192 = getelementptr i8, i8* %this, i32 8
	%_193 = bitcast i8* %_192 to i8**
	%_194 = load i8*, i8** %_193
	store i8* %_194, i8** %var_elem
	br label %loopInit1
loopInit1:
	%_195 = load i1, i1* %var_end
%_196 = xor i1 1, %_195
	br i1 %_196, label %loopStart1, label %loopEnd1
loopStart1:
	%_197 = load i8*, i8** %e
	; Element.Equal: 32
	%_198 = bitcast i8* %_197 to i8***
	%_199 = load i8**, i8*** %_198
	%_200 = getelementptr i8*, i8** %_199, i32 32
	%_201 = load i8*, i8** %_200
	%_202 = bitcast i8* %_201 to i1 (i8*,i8*)*
	%_203 = load i8*, i8** %var_elem
	%_204 = call i1 %_202(i8* %_197, i8* %_203)
	br i1 %_204, label %if10, label %else10
if10:
	store i32 1, i32* %int_ret_val

	br label %end10
else10:
	store i32 0, i32* %nt

	br label %end10

end10:
	%_205 = load i8*, i8** %aux01
	; List.GetNext: 64
	%_206 = bitcast i8* %_205 to i8***
	%_207 = load i8**, i8*** %_206
	%_208 = getelementptr i8*, i8** %_207, i32 64
	%_209 = load i8*, i8** %_208
	%_210 = bitcast i8* %_209 to i8* (i8*)*
	%_211 = call i8* %_210(i8* %_205)
	store i8* %_211, i8** %aux01
	%_212 = load i8*, i8** %aux01
	; List.GetEnd: 48
	%_213 = bitcast i8* %_212 to i8***
	%_214 = load i8**, i8*** %_213
	%_215 = getelementptr i8*, i8** %_214, i32 48
	%_216 = load i8*, i8** %_215
	%_217 = bitcast i8* %_216 to i1 (i8*)*
	%_218 = call i1 %_217(i8* %_212)
	store i1 %_218, i1* %var_end
	%_219 = load i8*, i8** %aux01
	; List.GetElem: 56
	%_220 = bitcast i8* %_219 to i8***
	%_221 = load i8**, i8*** %_220
	%_222 = getelementptr i8*, i8** %_221, i32 56
	%_223 = load i8*, i8** %_222
	%_224 = bitcast i8* %_223 to i8* (i8*)*
	%_225 = call i8* %_224(i8* %_219)
	store i8* %_225, i8** %var_elem
	br label %loopInit1
loopEnd1:
	%_226 = load i32, i32* %int_ret_val
	ret i32 %_226
}
define i1 @List.GetEnd(i8* %this) {

	%_227 = getelementptr i8, i8* %this, i32 24
	%_228 = bitcast i8* %_227 to i1*
	%_229 = load i1, i1* %_228
	ret i1 %_229
}
define i8* @List.GetElem(i8* %this) {

	%_230 = getelementptr i8, i8* %this, i32 8
	%_231 = bitcast i8* %_230 to i8**
	%_232 = load i8*, i8** %_231
	ret i8* %_232
}
define i8* @List.GetNext(i8* %this) {

	%_233 = getelementptr i8, i8* %this, i32 16
	%_234 = bitcast i8* %_233 to i8**
	%_235 = load i8*, i8** %_234
	ret i8* %_235
}
define i1 @List.Print(i8* %this) {

	%aux01 = alloca i8*
	%var_end = alloca i1
	%var_elem = alloca i8*
	store i8* %this, i8** %aux01
	%_236 = getelementptr i8, i8* %this, i32 24
	%_237 = bitcast i8* %_236 to i1*
	%_238 = load i1, i1* %_237
	store i1 %_238, i1* %var_end
	%_239 = getelementptr i8, i8* %this, i32 8
	%_240 = bitcast i8* %_239 to i8**
	%_241 = load i8*, i8** %_240
	store i8* %_241, i8** %var_elem
	br label %loopInit2
loopInit2:
	%_242 = load i1, i1* %var_end
%_243 = xor i1 1, %_242
	br i1 %_243, label %loopStart2, label %loopEnd2
loopStart2:
	%_244 = load i8*, i8** %var_elem
	; Element.GetAge: 8
	%_245 = bitcast i8* %_244 to i8***
	%_246 = load i8**, i8*** %_245
	%_247 = getelementptr i8*, i8** %_246, i32 8
	%_248 = load i8*, i8** %_247
	%_249 = bitcast i8* %_248 to i32 (i8*)*
	%_250 = call i32 %_249(i8* %_244)
	call void (i32) @print_int(i32 %_250)
	%_251 = load i8*, i8** %aux01
	; List.GetNext: 64
	%_252 = bitcast i8* %_251 to i8***
	%_253 = load i8**, i8*** %_252
	%_254 = getelementptr i8*, i8** %_253, i32 64
	%_255 = load i8*, i8** %_254
	%_256 = bitcast i8* %_255 to i8* (i8*)*
	%_257 = call i8* %_256(i8* %_251)
	store i8* %_257, i8** %aux01
	%_258 = load i8*, i8** %aux01
	; List.GetEnd: 48
	%_259 = bitcast i8* %_258 to i8***
	%_260 = load i8**, i8*** %_259
	%_261 = getelementptr i8*, i8** %_260, i32 48
	%_262 = load i8*, i8** %_261
	%_263 = bitcast i8* %_262 to i1 (i8*)*
	%_264 = call i1 %_263(i8* %_258)
	store i1 %_264, i1* %var_end
	%_265 = load i8*, i8** %aux01
	; List.GetElem: 56
	%_266 = bitcast i8* %_265 to i8***
	%_267 = load i8**, i8*** %_266
	%_268 = getelementptr i8*, i8** %_267, i32 56
	%_269 = load i8*, i8** %_268
	%_270 = bitcast i8* %_269 to i8* (i8*)*
	%_271 = call i8* %_270(i8* %_265)
	store i8* %_271, i8** %var_elem
	br label %loopInit2
loopEnd2:
	ret i1 1
}
@.LL_vtable = global [1 x i8*] [
				i8* bitcast (i32(i8*)* @LL.Start to i8*)
				]
define i32 @LL.Start(i8* %this) {

	%head = alloca i8*
	%last_elem = alloca i8*
	%aux01 = alloca i1
	%el01 = alloca i8*
	%el02 = alloca i8*
	%el03 = alloca i8*
	%_272 = call i8* @calloc(i32 1, i32 25)
	%_273 = bitcast i8* %_272 to i8***
	%_274 = getelementptr [10 x i8*], [10 x i8*]* @.List_vtable, i32 0, i32 0
	store i8** %_274, i8*** %_273
	store i8* %_272, i8** %last_elem
	%_275 = load i8*, i8** %last_elem
	; List.Init: 0
	%_276 = bitcast i8* %_275 to i8***
	%_277 = load i8**, i8*** %_276
	%_278 = getelementptr i8*, i8** %_277, i32 0
	%_279 = load i8*, i8** %_278
	%_280 = bitcast i8* %_279 to i1 (i8*)*
	%_281 = call i1 %_280(i8* %_275)
	store i1 %_281, i1* %aux01
	%_282 = load i8*, i8** %last_elem
	store i8* %_282, i8** %head
	%_283 = load i8*, i8** %head
	; List.Init: 0
	%_284 = bitcast i8* %_283 to i8***
	%_285 = load i8**, i8*** %_284
	%_286 = getelementptr i8*, i8** %_285, i32 0
	%_287 = load i8*, i8** %_286
	%_288 = bitcast i8* %_287 to i1 (i8*)*
	%_289 = call i1 %_288(i8* %_283)
	store i1 %_289, i1* %aux01
	%_290 = load i8*, i8** %head
	; List.Print: 72
	%_291 = bitcast i8* %_290 to i8***
	%_292 = load i8**, i8*** %_291
	%_293 = getelementptr i8*, i8** %_292, i32 72
	%_294 = load i8*, i8** %_293
	%_295 = bitcast i8* %_294 to i1 (i8*)*
	%_296 = call i1 %_295(i8* %_290)
	store i1 %_296, i1* %aux01
	%_297 = call i8* @calloc(i32 1, i32 17)
	%_298 = bitcast i8* %_297 to i8***
	%_299 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0, i32 0
	store i8** %_299, i8*** %_298
	store i8* %_297, i8** %el01
	%_300 = load i8*, i8** %el01
	; Element.Init: 0
	%_301 = bitcast i8* %_300 to i8***
	%_302 = load i8**, i8*** %_301
	%_303 = getelementptr i8*, i8** %_302, i32 0
	%_304 = load i8*, i8** %_303
	%_305 = bitcast i8* %_304 to i1 (i8*,i32,i32,i1)*
	%_306 = call i1 %_305(i8* %_300, i32 25, i32 37000, i1 0)
	store i1 %_306, i1* %aux01
	%_307 = load i8*, i8** %head
	; List.Insert: 16
	%_308 = bitcast i8* %_307 to i8***
	%_309 = load i8**, i8*** %_308
	%_310 = getelementptr i8*, i8** %_309, i32 16
	%_311 = load i8*, i8** %_310
	%_312 = bitcast i8* %_311 to i8* (i8*,i8*)*
	%_313 = load i8*, i8** %el01
	%_314 = call i8* %_312(i8* %_307, i8* %_313)
	store i8* %_314, i8** %head
	%_315 = load i8*, i8** %head
	; List.Print: 72
	%_316 = bitcast i8* %_315 to i8***
	%_317 = load i8**, i8*** %_316
	%_318 = getelementptr i8*, i8** %_317, i32 72
	%_319 = load i8*, i8** %_318
	%_320 = bitcast i8* %_319 to i1 (i8*)*
	%_321 = call i1 %_320(i8* %_315)
	store i1 %_321, i1* %aux01
	call void (i32) @print_int(i32 10000000)
	%_322 = call i8* @calloc(i32 1, i32 17)
	%_323 = bitcast i8* %_322 to i8***
	%_324 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0, i32 0
	store i8** %_324, i8*** %_323
	store i8* %_322, i8** %el01
	%_325 = load i8*, i8** %el01
	; Element.Init: 0
	%_326 = bitcast i8* %_325 to i8***
	%_327 = load i8**, i8*** %_326
	%_328 = getelementptr i8*, i8** %_327, i32 0
	%_329 = load i8*, i8** %_328
	%_330 = bitcast i8* %_329 to i1 (i8*,i32,i32,i1)*
	%_331 = call i1 %_330(i8* %_325, i32 39, i32 42000, i1 1)
	store i1 %_331, i1* %aux01
	%_332 = load i8*, i8** %el01
	store i8* %_332, i8** %el02
	%_333 = load i8*, i8** %head
	; List.Insert: 16
	%_334 = bitcast i8* %_333 to i8***
	%_335 = load i8**, i8*** %_334
	%_336 = getelementptr i8*, i8** %_335, i32 16
	%_337 = load i8*, i8** %_336
	%_338 = bitcast i8* %_337 to i8* (i8*,i8*)*
	%_339 = load i8*, i8** %el01
	%_340 = call i8* %_338(i8* %_333, i8* %_339)
	store i8* %_340, i8** %head
	%_341 = load i8*, i8** %head
	; List.Print: 72
	%_342 = bitcast i8* %_341 to i8***
	%_343 = load i8**, i8*** %_342
	%_344 = getelementptr i8*, i8** %_343, i32 72
	%_345 = load i8*, i8** %_344
	%_346 = bitcast i8* %_345 to i1 (i8*)*
	%_347 = call i1 %_346(i8* %_341)
	store i1 %_347, i1* %aux01
	call void (i32) @print_int(i32 10000000)
	%_348 = call i8* @calloc(i32 1, i32 17)
	%_349 = bitcast i8* %_348 to i8***
	%_350 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0, i32 0
	store i8** %_350, i8*** %_349
	store i8* %_348, i8** %el01
	%_351 = load i8*, i8** %el01
	; Element.Init: 0
	%_352 = bitcast i8* %_351 to i8***
	%_353 = load i8**, i8*** %_352
	%_354 = getelementptr i8*, i8** %_353, i32 0
	%_355 = load i8*, i8** %_354
	%_356 = bitcast i8* %_355 to i1 (i8*,i32,i32,i1)*
	%_357 = call i1 %_356(i8* %_351, i32 22, i32 34000, i1 0)
	store i1 %_357, i1* %aux01
	%_358 = load i8*, i8** %head
	; List.Insert: 16
	%_359 = bitcast i8* %_358 to i8***
	%_360 = load i8**, i8*** %_359
	%_361 = getelementptr i8*, i8** %_360, i32 16
	%_362 = load i8*, i8** %_361
	%_363 = bitcast i8* %_362 to i8* (i8*,i8*)*
	%_364 = load i8*, i8** %el01
	%_365 = call i8* %_363(i8* %_358, i8* %_364)
	store i8* %_365, i8** %head
	%_366 = load i8*, i8** %head
	; List.Print: 72
	%_367 = bitcast i8* %_366 to i8***
	%_368 = load i8**, i8*** %_367
	%_369 = getelementptr i8*, i8** %_368, i32 72
	%_370 = load i8*, i8** %_369
	%_371 = bitcast i8* %_370 to i1 (i8*)*
	%_372 = call i1 %_371(i8* %_366)
	store i1 %_372, i1* %aux01
	%_373 = call i8* @calloc(i32 1, i32 17)
	%_374 = bitcast i8* %_373 to i8***
	%_375 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0, i32 0
	store i8** %_375, i8*** %_374
	store i8* %_373, i8** %el03
	%_376 = load i8*, i8** %el03
	; Element.Init: 0
	%_377 = bitcast i8* %_376 to i8***
	%_378 = load i8**, i8*** %_377
	%_379 = getelementptr i8*, i8** %_378, i32 0
	%_380 = load i8*, i8** %_379
	%_381 = bitcast i8* %_380 to i1 (i8*,i32,i32,i1)*
	%_382 = call i1 %_381(i8* %_376, i32 27, i32 34000, i1 0)
	store i1 %_382, i1* %aux01
	%_383 = load i8*, i8** %head
	; List.Search: 40
	%_384 = bitcast i8* %_383 to i8***
	%_385 = load i8**, i8*** %_384
	%_386 = getelementptr i8*, i8** %_385, i32 40
	%_387 = load i8*, i8** %_386
	%_388 = bitcast i8* %_387 to i32 (i8*,i8*)*
	%_389 = load i8*, i8** %el02
	%_390 = call i32 %_388(i8* %_383, i8* %_389)
	call void (i32) @print_int(i32 %_390)
	%_391 = load i8*, i8** %head
	; List.Search: 40
	%_392 = bitcast i8* %_391 to i8***
	%_393 = load i8**, i8*** %_392
	%_394 = getelementptr i8*, i8** %_393, i32 40
	%_395 = load i8*, i8** %_394
	%_396 = bitcast i8* %_395 to i32 (i8*,i8*)*
	%_397 = load i8*, i8** %el03
	%_398 = call i32 %_396(i8* %_391, i8* %_397)
	call void (i32) @print_int(i32 %_398)
	call void (i32) @print_int(i32 10000000)
	%_399 = call i8* @calloc(i32 1, i32 17)
	%_400 = bitcast i8* %_399 to i8***
	%_401 = getelementptr [6 x i8*], [6 x i8*]* @.Element_vtable, i32 0, i32 0
	store i8** %_401, i8*** %_400
	store i8* %_399, i8** %el01
	%_402 = load i8*, i8** %el01
	; Element.Init: 0
	%_403 = bitcast i8* %_402 to i8***
	%_404 = load i8**, i8*** %_403
	%_405 = getelementptr i8*, i8** %_404, i32 0
	%_406 = load i8*, i8** %_405
	%_407 = bitcast i8* %_406 to i1 (i8*,i32,i32,i1)*
	%_408 = call i1 %_407(i8* %_402, i32 28, i32 35000, i1 0)
	store i1 %_408, i1* %aux01
	%_409 = load i8*, i8** %head
	; List.Insert: 16
	%_410 = bitcast i8* %_409 to i8***
	%_411 = load i8**, i8*** %_410
	%_412 = getelementptr i8*, i8** %_411, i32 16
	%_413 = load i8*, i8** %_412
	%_414 = bitcast i8* %_413 to i8* (i8*,i8*)*
	%_415 = load i8*, i8** %el01
	%_416 = call i8* %_414(i8* %_409, i8* %_415)
	store i8* %_416, i8** %head
	%_417 = load i8*, i8** %head
	; List.Print: 72
	%_418 = bitcast i8* %_417 to i8***
	%_419 = load i8**, i8*** %_418
	%_420 = getelementptr i8*, i8** %_419, i32 72
	%_421 = load i8*, i8** %_420
	%_422 = bitcast i8* %_421 to i1 (i8*)*
	%_423 = call i1 %_422(i8* %_417)
	store i1 %_423, i1* %aux01
	call void (i32) @print_int(i32 2220000)
	%_424 = load i8*, i8** %head
	; List.Delete: 32
	%_425 = bitcast i8* %_424 to i8***
	%_426 = load i8**, i8*** %_425
	%_427 = getelementptr i8*, i8** %_426, i32 32
	%_428 = load i8*, i8** %_427
	%_429 = bitcast i8* %_428 to i8* (i8*,i8*)*
	%_430 = load i8*, i8** %el02
	%_431 = call i8* %_429(i8* %_424, i8* %_430)
	store i8* %_431, i8** %head
	%_432 = load i8*, i8** %head
	; List.Print: 72
	%_433 = bitcast i8* %_432 to i8***
	%_434 = load i8**, i8*** %_433
	%_435 = getelementptr i8*, i8** %_434, i32 72
	%_436 = load i8*, i8** %_435
	%_437 = bitcast i8* %_436 to i1 (i8*)*
	%_438 = call i1 %_437(i8* %_432)
	store i1 %_438, i1* %aux01
	call void (i32) @print_int(i32 33300000)
	%_439 = load i8*, i8** %head
	; List.Delete: 32
	%_440 = bitcast i8* %_439 to i8***
	%_441 = load i8**, i8*** %_440
	%_442 = getelementptr i8*, i8** %_441, i32 32
	%_443 = load i8*, i8** %_442
	%_444 = bitcast i8* %_443 to i8* (i8*,i8*)*
	%_445 = load i8*, i8** %el01
	%_446 = call i8* %_444(i8* %_439, i8* %_445)
	store i8* %_446, i8** %head
	%_447 = load i8*, i8** %head
	; List.Print: 72
	%_448 = bitcast i8* %_447 to i8***
	%_449 = load i8**, i8*** %_448
	%_450 = getelementptr i8*, i8** %_449, i32 72
	%_451 = load i8*, i8** %_450
	%_452 = bitcast i8* %_451 to i1 (i8*)*
	%_453 = call i1 %_452(i8* %_447)
	store i1 %_453, i1* %aux01
	call void (i32) @print_int(i32 44440000)
	ret i32 0
}
