-- ============================================================
-- SL323-2011 表90 河道水情极值表 (ST_RVEVS_R)
-- 存储测站在某一统计时段内的水位/流量极值及出现时间
-- ============================================================
CREATE TABLE ST_RVEVS_R (
    STCD    VARCHAR(8)  NOT NULL,                  -- 测站编码
    IDTM    DATETIME    NOT NULL,                  -- 标志时间（统计时段截止后的次日零点）
    STTDRCD VARCHAR(1)  NOT NULL,                  -- 统计时段标志（1=一日 2=三日 3=一侯 4=一旬 5=一月 6=一年）
    HTZ     DECIMAL(7,3),                          -- 最高水位（单位：m）
    LTZ     DECIMAL(7,3),                          -- 最低水位（单位：m）
    MXQ     DECIMAL(9,3),                          -- 最大流量（单位：m³/s）
    MNQ     DECIMAL(9,3),                          -- 最小流量（单位：m³/s）
    HTZTM   DATETIME,                              -- 最高水位出现时间
    LTZTM   DATETIME,                              -- 最低水位出现时间
    MXQTM   DATETIME,                              -- 最大流量出现时间
    MNQTM   DATETIME,                              -- 最小流量出现时间
    PRIMARY KEY (IDTM, STCD, STTDRCD)
);

COMMENT ON TABLE  ST_RVEVS_R             IS '河道水情极值表（SL323-2011 表90）';
COMMENT ON COLUMN ST_RVEVS_R.STCD        IS '测站编码';
COMMENT ON COLUMN ST_RVEVS_R.IDTM        IS '标志时间（统计时段截止后的次日零点）';
COMMENT ON COLUMN ST_RVEVS_R.STTDRCD     IS '统计时段标志（1=一日 2=三日 3=一侯 4=一旬 5=一月 6=一年）';
COMMENT ON COLUMN ST_RVEVS_R.HTZ         IS '最高水位（m）';
COMMENT ON COLUMN ST_RVEVS_R.LTZ         IS '最低水位（m）';
COMMENT ON COLUMN ST_RVEVS_R.MXQ         IS '最大流量（m³/s）';
COMMENT ON COLUMN ST_RVEVS_R.MNQ         IS '最小流量（m³/s）';
COMMENT ON COLUMN ST_RVEVS_R.HTZTM       IS '最高水位出现时间';
COMMENT ON COLUMN ST_RVEVS_R.LTZTM       IS '最低水位出现时间';
COMMENT ON COLUMN ST_RVEVS_R.MXQTM       IS '最大流量出现时间';
COMMENT ON COLUMN ST_RVEVS_R.MNQTM       IS '最小流量出现时间';


-- ============================================================
-- SL323-2011 表79 河道水情多日均值表 (ST_RVAV_R)
-- 存储测站在某一统计时段内的水位/流量平均值
-- ============================================================
CREATE TABLE ST_RVAV_R (
    STCD    VARCHAR(8)  NOT NULL,                  -- 测站编码
    IDTM    DATETIME    NOT NULL,                  -- 标志时间（统计时段截止后的次日零点）
    STTDRCD VARCHAR(1)  NOT NULL,                  -- 统计时段标志（1=一日 2=三日 3=一侯 4=一旬 5=一月 6=一年）
    AVZ     DECIMAL(7,3),                          -- 平均水位（单位：m）
    AVQ     DECIMAL(9,3),                          -- 平均流量（单位：m³/s）
    PRIMARY KEY (IDTM, STCD, STTDRCD)
);

COMMENT ON TABLE  ST_RVAV_R             IS '河道水情多日均值表（SL323-2011 表79）';
COMMENT ON COLUMN ST_RVAV_R.STCD        IS '测站编码';
COMMENT ON COLUMN ST_RVAV_R.IDTM        IS '标志时间（统计时段截止后的次日零点）';
COMMENT ON COLUMN ST_RVAV_R.STTDRCD     IS '统计时段标志（1=一日 2=三日 3=一侯 4=一旬 5=一月 6=一年）';
COMMENT ON COLUMN ST_RVAV_R.AVZ         IS '平均水位（m）';
COMMENT ON COLUMN ST_RVAV_R.AVQ         IS '平均流量（m³/s）';
