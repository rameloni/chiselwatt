import Control.FxmRegs
import chisel3._
import chisel3.util.ListLookup
import circt.stage.ChiselStage

object Control {
  val Y = true.B
  val N = false.B

  val DC = false.B

  // Unit
  object Unit extends ChiselEnum {
    val U_ADD, U_LOG, U_ROT, U_MUL, U_DIV, U_LDST, U_BR, U_CR, U_ZER, U_POP, U_SPR, U_ILL, U_NONE = Value
  }

  // Internal ops
  object InternalOps extends ChiselEnum {
    val DC, LOG_AND, LOG_OR, LOG_XOR, LOG_EXTS, LDST_LOAD, LDST_STORE, DIV_DIV, DIV_MOD, SPR_MF, SPR_MT, CR_MF, CR_MT, CR_MCRF, BR_UNCOND, BR_COND = Value
  }


  // Input registers
  object RaRegs extends ChiselEnum {
    val DC, RA_RA, RA_RA_OR_ZERO, RA_ZERO = Value
  }

  object RbRegs extends ChiselEnum {
    val DC, RB_RB, RB_CONST_UI, RB_CONST_SI, RB_CONST_SI_HI, RB_CONST_UI_HI, RB_CONST_DS, RB_CONST_M1, RB_CONST_ZERO, RB_CONST_SH, RB_CONST_SH32 = Value
  }

  object RsRegs extends ChiselEnum {
    val DC, RS_RS = Value
  }

  // Output registers
  object RoutRegs extends ChiselEnum {
    val DC, ROUT_NONE, ROUT_RT, ROUT_RA = Value
  }

  object CmpRegs extends ChiselEnum {
    val DC, CMP_RC_0, CMP_RC_RC, CMP_RC_1, CMP_CMP = Value
  }

  object CaRegs extends ChiselEnum {
    val DC, CA_0, CA_CA, CA_1 = Value
  }

  object LenEnum extends ChiselEnum {
    val DC, LEN_1B, LEN_2B, LEN_4B, LEN_8B = Value
  }

  object FxmRegs extends ChiselEnum {
    val DC, FXM, FXM_FF, FXM_ONEHOT = Value
  }

  object BrTarget extends ChiselEnum {
    val DC, BR_TARGET_NONE, BR_TARGET_CTR, BR_TARGET_LR = Value
  }

  import Instructions._
  import Unit._
  import InternalOps._
  import RaRegs._
  import RbRegs._
  import RsRegs._
  import RoutRegs._
  import CmpRegs._
  import FxmRegs._
  import BrTarget._
  import LenEnum._


  val default =
                     List(U_ILL,  InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,   DC,       DC,       DC,        DC,       DC,        LenEnum.DC,             DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC)

  val map = Array(
                       // unit    internalOp       rA             rB              rS            rOut          carryIn        carryOut crIn crOut compare        is32bit signed invertIn invertOut rightShift clearLeft clearRight length          byteReverse update reservation high extended countRight fxm         brTarget
    ADDIC         -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   Y,       DC,  DC,   CMP_RC_0,      DC,     DC,    N,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ADDIC_DOT     -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   Y,       DC,  DC,   CMP_RC_1,      DC,     DC,    N,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ADDI          -> List(U_ADD,  InternalOps.DC,  RA_RA_OR_ZERO, RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   N,       DC,  DC,   CMP_RC_0,      DC,     DC,    N,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ADDIS         -> List(U_ADD,  InternalOps.DC,  RA_RA_OR_ZERO, RB_CONST_SI_HI, RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   N,       DC,  DC,   CMP_RC_0,      DC,     DC,    N,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SUBFIC        -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_1,   Y,       DC,  DC,   CMP_RC_0,      DC,     DC,    Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ADD           -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   N,       DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ADDC          -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   Y,       DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ADDE          -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_CA,  Y,       DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ADDME         -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_M1,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_CA,  Y,       DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ADDZE         -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_ZERO,  RsRegs.DC,    ROUT_RT,      CaRegs.CA_CA,  Y,       DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SUBF          -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_1,   N,       DC,  DC,   CMP_RC_RC,     DC,     DC,    Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SUBFC         -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_1,   Y,       DC,  DC,   CMP_RC_RC,     DC,     DC,    Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SUBFE         -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_CA,  Y,       DC,  DC,   CMP_RC_RC,     DC,     DC,    Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SUBFME        -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_M1,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_CA,  Y,       DC,  DC,   CMP_RC_RC,     DC,     DC,    Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SUBFZE        -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_ZERO,  RsRegs.DC,    ROUT_RT,      CaRegs.CA_CA,  Y,       DC,  DC,   CMP_RC_RC,     DC,     DC,    Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    NEG           -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_ZERO,  RsRegs.DC,    ROUT_RT,      CaRegs.CA_1,   N,       DC,  DC,   CMP_RC_RC,     DC,     DC,    Y,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ANDI_DOT      -> List(U_LOG,  LOG_AND,         RaRegs.DC,     RB_CONST_UI,    RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_1,      DC,     DC,    N,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ANDIS_DOT     -> List(U_LOG,  LOG_AND,         RaRegs.DC,     RB_CONST_UI_HI, RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_1,      DC,     DC,    N,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    AND           -> List(U_LOG,  LOG_AND,         RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ANDC          -> List(U_LOG,  LOG_AND,         RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    Y,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    NAND          -> List(U_LOG,  LOG_AND,         RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       Y,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ORI           -> List(U_LOG,  LOG_OR,          RaRegs.DC,     RB_CONST_UI,    RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    N,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ORIS          -> List(U_LOG,  LOG_OR,          RaRegs.DC,     RB_CONST_UI_HI, RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    N,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    NOR           -> List(U_LOG,  LOG_OR,          RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       Y,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    OR            -> List(U_LOG,  LOG_OR,          RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ORC           -> List(U_LOG,  LOG_OR,          RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    Y,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    XORI          -> List(U_LOG,  LOG_XOR,         RaRegs.DC,     RB_CONST_UI,    RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    N,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    XORIS         -> List(U_LOG,  LOG_XOR,         RaRegs.DC,     RB_CONST_UI_HI, RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    N,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    EQV           -> List(U_LOG,  LOG_XOR,         RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       Y,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    XOR           -> List(U_LOG,  LOG_XOR,         RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       N,        DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    EXTSB         -> List(U_LOG,  LOG_EXTS,        RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       N,        DC,        DC,       DC,        LEN_1B, DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    EXTSH         -> List(U_LOG,  LOG_EXTS,        RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       N,        DC,        DC,       DC,        LEN_2B, DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    EXTSW         -> List(U_LOG,  LOG_EXTS,        RaRegs.DC,     RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     DC,     DC,    N,       N,        DC,        DC,       DC,        LEN_4B, DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    RLWIMI        -> List(U_ROT,  InternalOps.DC,  RA_RA,         RB_CONST_SH32,  RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     Y,      N,     DC,      DC,       N,         Y,        Y,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    RLWINM        -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_CONST_SH32,  RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     Y,      N,     DC,      DC,       N,         Y,        Y,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    RLWNM         -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     Y,      N,     DC,      DC,       N,         Y,        Y,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    RLDIC         -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_CONST_SH,    RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       N,         Y,        Y,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    RLDICL        -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_CONST_SH,    RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       N,         Y,        N,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    RLDICR        -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_CONST_SH,    RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       N,         N,        Y,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    RLDIMI        -> List(U_ROT,  InternalOps.DC,  RA_RA,         RB_CONST_SH,    RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       N,         Y,        Y,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    RLDCL         -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       N,         Y,        N,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    RLDCR         -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       N,         N,        Y,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SLD           -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       N,         N,        N,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SLW           -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     Y,      N,     DC,      DC,       N,         N,        N,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SRAD          -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     Y,       DC,  DC,   CMP_RC_RC,     N,      Y,     DC,      DC,       Y,         N,        N,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SRADI         -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_CONST_SH,    RS_RS,        ROUT_RA,      CaRegs.DC,     Y,       DC,  DC,   CMP_RC_RC,     N,      Y,     DC,      DC,       Y,         N,        N,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SRAW          -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     Y,       DC,  DC,   CMP_RC_RC,     Y,      Y,     DC,      DC,       Y,         N,        N,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SRAWI         -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_CONST_SH32,  RS_RS,        ROUT_RA,      CaRegs.DC,     Y,       DC,  DC,   CMP_RC_RC,     Y,      Y,     DC,      DC,       Y,         N,        N,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SRD           -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       Y,         N,        N,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    SRW           -> List(U_ROT,  InternalOps.DC,  RA_ZERO,       RB_RB,          RS_RS,        ROUT_RA,      CaRegs.DC,     N,       DC,  DC,   CMP_RC_RC,     Y,      N,     DC,      DC,       Y,         N,        N,         LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LBZ           -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_1B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LBZU          -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_1B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LHA           -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     Y,     DC,      DC,       DC,        DC,       DC,        LEN_2B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LHAU          -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     Y,     N,       DC,       DC,        DC,       DC,        LEN_2B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LHZ           -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_2B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LHZU          -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_2B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LWA           -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_DS,    RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     Y,     DC,      DC,       DC,        DC,       DC,        LEN_4B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LWZ           -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_4B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LWZU          -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_4B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LD            -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_DS,    RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_8B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LDU           -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_CONST_DS,    RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_8B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LBARX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_1B,         N,          N,     Y,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LBZX          -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_1B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LBZUX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_1B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LHARX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_2B,         N,          N,     Y,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LHAX          -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     Y,     DC,      DC,       DC,        DC,       DC,        LEN_2B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LHAUX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     Y,     N,       DC,       DC,        DC,       DC,        LEN_2B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LHBRX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_2B,         Y,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LHZX          -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_2B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LHZUX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_2B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LWARX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_4B,         N,          N,     Y,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LWAX          -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     Y,     DC,      DC,       DC,        DC,       DC,        LEN_4B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LWAUX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     Y,     N,       DC,       DC,        DC,       DC,        LEN_4B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LWBRX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_4B,         Y,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LWZX          -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_4B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LWZUX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_4B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LDARX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_8B,         N,          N,     Y,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LDBRX         -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_8B,         Y,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LDX           -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_8B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    LDUX          -> List(U_LDST, LDST_LOAD,       RA_RA_OR_ZERO, RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_8B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STB           -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_CONST_SI,    RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_1B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STBU          -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_CONST_SI,    RS_RS,        ROUT_NONE,    CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_1B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STH           -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_CONST_SI,    RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_2B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STHU          -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_CONST_SI,    RS_RS,        ROUT_NONE,    CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_2B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STW           -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_CONST_SI,    RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_4B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STWU          -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_CONST_SI,    RS_RS,        ROUT_NONE,    CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_4B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STD           -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_CONST_DS,    RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_8B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STDU          -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_CONST_DS,    RS_RS,        ROUT_NONE,    CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_8B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STBCX_DOT     -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_1B,         N,          N,     Y,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STBX          -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_1B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STBUX         -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_1B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STHBRX        -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_2B,         Y,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STHCX_DOT     -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_2B,         N,          N,     Y,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STHX          -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_2B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STHUX         -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_2B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STWBRX        -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_4B,         Y,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STWCX_DOT     -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_4B,         N,          N,     Y,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STWX          -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_4B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STWUX         -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_4B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STDBRX        -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_8B,         Y,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STDCX_DOT     -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_8B,         N,          N,     Y,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STDX          -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     N,     DC,      DC,       DC,        DC,       DC,        LEN_8B,         N,          N,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    STDUX         -> List(U_LDST, LDST_STORE,      RA_RA_OR_ZERO, RB_RB,          RS_RS,        ROUT_NONE,    CaRegs.CA_0,   DC,      DC,  DC,   CMP_RC_0,      DC,     N,     N,       DC,       DC,        DC,       DC,        LEN_8B,         N,          Y,     N,          DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    MULLI         -> List(U_MUL,  InternalOps.DC,  RA_RA,         RB_CONST_SI,    RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      N,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         N,   DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    MULHD         -> List(U_MUL,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         Y,   DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    MULHDU        -> List(U_MUL,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         Y,   DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    MULHW         -> List(U_MUL,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         Y,   DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    MULHWU        -> List(U_MUL,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      N,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         Y,   DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    MULLD         -> List(U_MUL,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         N,   DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    MULLW         -> List(U_MUL,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         N,   DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    DIVDEU        -> List(U_DIV,  DIV_DIV,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  Y,       DC,        FxmRegs.DC,         BrTarget.DC),
    DIVWEU        -> List(U_DIV,  DIV_DIV,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      N,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  Y,       DC,        FxmRegs.DC,         BrTarget.DC),
    DIVDE         -> List(U_DIV,  DIV_DIV,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  Y,       DC,        FxmRegs.DC,         BrTarget.DC),
    DIVWE         -> List(U_DIV,  DIV_DIV,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  Y,       DC,        FxmRegs.DC,         BrTarget.DC),
    DIVDU         -> List(U_DIV,  DIV_DIV,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  N,       DC,        FxmRegs.DC,         BrTarget.DC),
    DIVWU         -> List(U_DIV,  DIV_DIV,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      N,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  N,       DC,        FxmRegs.DC,         BrTarget.DC),
    DIVD          -> List(U_DIV,  DIV_DIV,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  N,       DC,        FxmRegs.DC,         BrTarget.DC),
    DIVW          -> List(U_DIV,  DIV_DIV,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  N,       DC,        FxmRegs.DC,         BrTarget.DC),
    MODUD         -> List(U_DIV,  DIV_MOD,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      N,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  N,       DC,        FxmRegs.DC,         BrTarget.DC),
    MODUW         -> List(U_DIV,  DIV_MOD,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      N,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  N,       DC,        FxmRegs.DC,         BrTarget.DC),
    MODSD         -> List(U_DIV,  DIV_MOD,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  N,       DC,        FxmRegs.DC,         BrTarget.DC),
    MODSW         -> List(U_DIV,  DIV_MOD,         RA_RA,         RB_RB,          RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      Y,     DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  N,       DC,        FxmRegs.DC,         BrTarget.DC),
    SYNC          -> List(U_NONE, InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ISYNC         -> List(U_NONE, InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    DCBF          -> List(U_NONE, InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    DCBST         -> List(U_NONE, InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    DCBT          -> List(U_NONE, InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    DCBTST        -> List(U_NONE, InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ICBI          -> List(U_NONE, InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    ICBT          -> List(U_NONE, InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    CNTLZD        -> List(U_ZER,  InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      N,         FxmRegs.DC,         BrTarget.DC),
    CNTLZW        -> List(U_ZER,  InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      N,         FxmRegs.DC,         BrTarget.DC),
    CNTTZD        -> List(U_ZER,  InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     N,      DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      Y,         FxmRegs.DC,         BrTarget.DC),
    CNTTZW        -> List(U_ZER,  InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_RC,     Y,      DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      Y,         FxmRegs.DC,         BrTarget.DC),
    POPCNTB       -> List(U_POP,  InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LEN_1B,         DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    POPCNTW       -> List(U_POP,  InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LEN_4B,         DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    POPCNTD       -> List(U_POP,  InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RS_RS,        ROUT_RA,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LEN_8B,         DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    CMPDI         -> List(U_ADD,  InternalOps.DC,  RaRegs.RA_RA,  RB_CONST_SI,    RsRegs.DC,    ROUT_NONE,    CaRegs.CA_1,   DC,      DC,  Y,    CMP_CMP,       N,      Y,     Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    CMPWI         -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_SI,    RsRegs.DC,    ROUT_NONE,    CaRegs.CA_1,   DC,      DC,  Y,    CMP_CMP,       Y,      Y,     Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    CMPD          -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_NONE,    CaRegs.CA_1,   DC,      DC,  Y,    CMP_CMP,       N,      Y,     Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    CMPW          -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_NONE,    CaRegs.CA_1,   DC,      DC,  Y,    CMP_CMP,       Y,      Y,     Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    CMPLDI        -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_UI,    RsRegs.DC,    ROUT_NONE,    CaRegs.CA_1,   DC,      DC,  Y,    CMP_CMP,       N,      N,     Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    CMPLWI        -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_CONST_UI,    RsRegs.DC,    ROUT_NONE,    CaRegs.CA_1,   DC,      DC,  Y,    CMP_CMP,       Y,      N,     Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    CMPLD         -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_NONE,    CaRegs.CA_1,   DC,      DC,  Y,    CMP_CMP,       N,      N,     Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    CMPLW         -> List(U_ADD,  InternalOps.DC,  RA_RA,         RB_RB,          RsRegs.DC,    ROUT_NONE,    CaRegs.CA_1,   DC,      DC,  Y,    CMP_CMP,       Y,      N,     Y,       DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    MFSPR         -> List(U_SPR,  SPR_MF,          RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    MTSPR         -> List(U_SPR,  SPR_MT,          RaRegs.DC,     RbRegs.DC,      RsRegs.RS_RS, ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    MFCR          -> List(U_CR,   CR_MF,           RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FXM_FF,             BrTarget.DC),
    MFOCRF        -> List(U_CR,   CR_MF,           RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    ROUT_RT,      CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FXM_ONEHOT,         BrTarget.DC),
    MTCRF         -> List(U_CR,   CR_MT,           RaRegs.DC,     RbRegs.DC,      RsRegs.RS_RS, ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FXM,                BrTarget.DC),
    MTOCRF        -> List(U_CR,   CR_MT,           RaRegs.DC,     RbRegs.DC,      RsRegs.RS_RS, ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FXM_ONEHOT,         BrTarget.DC),
    MCRF          -> List(U_CR,   CR_MCRF,         RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    ROUT_NONE,    CaRegs.DC,     DC,      DC,  DC,   CMP_RC_0,      DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC),
    B             -> List(U_BR,   BR_UNCOND,       RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CmpRegs.DC,    DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BR_TARGET_NONE),
    BC            -> List(U_BR,   BR_COND,         RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CmpRegs.DC,    DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BR_TARGET_NONE),
    BCLR          -> List(U_BR,   BR_COND,         RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CmpRegs.DC,    DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BR_TARGET_LR),
    BCCTR         -> List(U_BR,   BR_COND,         RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CmpRegs.DC,    DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BR_TARGET_CTR),
    TDI           -> List(U_NONE, InternalOps.DC,  RaRegs.DC,     RbRegs.DC,      RsRegs.DC,    RoutRegs.DC,  CaRegs.DC,     DC,      DC,  DC,   CmpRegs.DC,    DC,     DC,    DC,      DC,       DC,        DC,       DC,        LenEnum.DC,     DC,         DC,    DC,         DC,  DC,      DC,        FxmRegs.DC,         BrTarget.DC)
  )
}

class ControlSignals extends Bundle {
  val unit        = Control.Unit()
  val internalOp  = Control.InternalOps()
  val rA          = Control.RaRegs()
  val rB          = Control.RbRegs()
  val rS          = Control.RsRegs()
  val rOut        = Control.RoutRegs()
  val carryIn     = Control.CaRegs()
  val carryOut    = UInt(1.W)
  val crIn        = UInt(1.W)
  val crOut       = UInt(1.W)
  val compare     = Control.CmpRegs()
  val is32bit     = UInt(1.W)
  val signed      = UInt(1.W)
  val invertIn    = UInt(1.W)
  val invertOut   = UInt(1.W)
  val rightShift  = UInt(1.W)
  val clearLeft   = UInt(1.W)
  val clearRight  = UInt(1.W)
  val length      = Control.LenEnum()
  val byteReverse = UInt(1.W)
  val update      = UInt(1.W)
  val reservation = UInt(1.W)
  val high        = UInt(1.W)
  val extended    = UInt(1.W)
  val countRight  = UInt(1.W)
  val fxm         = Control.FxmRegs()
  val brTarget    = Control.BrTarget()
}

class Control(val n: Int) extends Module {
  val io = IO(new Bundle {
    val insn = Input(UInt(32.W))
    val out = Output(new ControlSignals())
  })

  val ctrlSignals = ListLookup(io.insn, Control.default, Control.map)

  io.out.unit        := ctrlSignals(0)
  io.out.internalOp  := ctrlSignals(1)
  io.out.rA          := ctrlSignals(2)
  io.out.rB          := ctrlSignals(3)
  io.out.rS          := ctrlSignals(4)
  io.out.rOut        := ctrlSignals(5)
  io.out.carryIn     := ctrlSignals(6)
  io.out.carryOut    := ctrlSignals(7)
  io.out.crIn        := ctrlSignals(8)
  io.out.crOut       := ctrlSignals(9)
  io.out.compare     := ctrlSignals(10)
  io.out.is32bit     := ctrlSignals(11)
  io.out.signed      := ctrlSignals(12)
  io.out.invertIn    := ctrlSignals(13)
  io.out.invertOut   := ctrlSignals(14)
  io.out.rightShift  := ctrlSignals(15)
  io.out.clearLeft   := ctrlSignals(16)
  io.out.clearRight  := ctrlSignals(17)
  io.out.length      := ctrlSignals(18)
  io.out.byteReverse := ctrlSignals(19)
  io.out.update      := ctrlSignals(20)
  io.out.reservation := ctrlSignals(21)
  io.out.high        := ctrlSignals(22)
  io.out.extended    := ctrlSignals(23)
  io.out.countRight  := ctrlSignals(24)
  io.out.fxm         := ctrlSignals(25)
  io.out.brTarget    := ctrlSignals(26)
}

object ControlObj extends App {
  ChiselStage.emitSystemVerilogFile(new Control(64))
}
