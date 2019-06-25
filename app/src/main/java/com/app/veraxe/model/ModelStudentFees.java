package com.app.veraxe.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModelStudentFees {


    /**
     * response : 1
     * data : {"feeSchedule":"Monthy (Apr)","fees":[{"headerName":"Tution","cycle":"Monthy","amount":"1100.00"},{"headerName":"ABC Fee","cycle":"Monthy","amount":"1200.00"},{"headerName":"Development Fee","cycle":"Monthy","amount":"1300.00"},{"headerName":"Transport Fee","cycle":"Monthy","amount":"100.00"}],"extra":[],"discount":[],"summery":{"subtotal":"3700.00","balance":"0.00","extra":"0.00","discount":"0.00","grandTotal":"3700.00","servicePercentage":"4.00","serviceAmount":"148.00","payableAmount":"3848.00"},"receiptData":{"fee":{"studentId":"3123","classId":"145","sectionId":"318","streamId":"293","schoolId":"6","sessionYearId":"3","total":"3700.00"},"feesMeta":[[{"cycle_id":1,"cycle_no":1,"fee":"3600.00","transport_fee":"100.00","discount":"0.00","extra":"0.00","total":"3700.00","paid":"3700.00","balance":"0.00"}]],"feeCycleMeta":[{"cycle_id":1,"discount":"0.00","extra":"0.00","total":"3700.00","paid":"3700.00","balance":"0.00"}]}}
     */

    private int response;
    private DataBean data;

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * feeSchedule : Monthy (Apr)
         * fees : [{"headerName":"Tution","cycle":"Monthy","amount":"1100.00"},{"headerName":"ABC Fee","cycle":"Monthy","amount":"1200.00"},{"headerName":"Development Fee","cycle":"Monthy","amount":"1300.00"},{"headerName":"Transport Fee","cycle":"Monthy","amount":"100.00"}]
         * extra : []
         * discount : []
         * summery : {"subtotal":"3700.00","balance":"0.00","extra":"0.00","discount":"0.00","grandTotal":"3700.00","servicePercentage":"4.00","serviceAmount":"148.00","payableAmount":"3848.00"}
         * receiptData : {"fee":{"studentId":"3123","classId":"145","sectionId":"318","streamId":"293","schoolId":"6","sessionYearId":"3","total":"3700.00"},"feesMeta":[[{"cycle_id":1,"cycle_no":1,"fee":"3600.00","transport_fee":"100.00","discount":"0.00","extra":"0.00","total":"3700.00","paid":"3700.00","balance":"0.00"}]],"feeCycleMeta":[{"cycle_id":1,"discount":"0.00","extra":"0.00","total":"3700.00","paid":"3700.00","balance":"0.00"}]}
         */

        private String feeSchedule;
        private SummeryBean summery;
        private ReceiptDataBean receiptData;
        private List<FeesBean> fees;
        private List<FeesBean> extra;
        private List<FeesBean> discount;

        public String getFeeSchedule() {
            return feeSchedule;
        }

        public void setFeeSchedule(String feeSchedule) {
            this.feeSchedule = feeSchedule;
        }

        public SummeryBean getSummery() {
            return summery;
        }

        public void setSummery(SummeryBean summery) {
            this.summery = summery;
        }

        public ReceiptDataBean getReceiptData() {
            return receiptData;
        }

        public void setReceiptData(ReceiptDataBean receiptData) {
            this.receiptData = receiptData;
        }

        public List<FeesBean> getFees() {
            return fees;
        }

        public void setFees(List<FeesBean> fees) {
            this.fees = fees;
        }

        public List<FeesBean> getExtra() {
            return extra;
        }

        public void setExtra(List<FeesBean> extra) {
            this.extra = extra;
        }

        public List<FeesBean> getDiscount() {
            return discount;
        }

        public void setDiscount(List<FeesBean> discount) {
            this.discount = discount;
        }

        public static class SummeryBean {
            /**
             * subtotal : 3700.00
             * balance : 0.00
             * extra : 0.00
             * discount : 0.00
             * grandTotal : 3700.00
             * servicePercentage : 4.00
             * serviceAmount : 148.00
             * payableAmount : 3848.00
             */

            private String subtotal;
            private String balance;
            private String extra;
            private String discount;
            private String grandTotal;
            private String servicePercentage;
            private String serviceAmount;
            private String payableAmount;

            public String getSubtotal() {
                return subtotal;
            }

            public void setSubtotal(String subtotal) {
                this.subtotal = subtotal;
            }

            public String getBalance() {
                return balance;
            }

            public void setBalance(String balance) {
                this.balance = balance;
            }

            public String getExtra() {
                return extra;
            }

            public void setExtra(String extra) {
                this.extra = extra;
            }

            public String getDiscount() {
                return discount;
            }

            public void setDiscount(String discount) {
                this.discount = discount;
            }

            public String getGrandTotal() {
                return grandTotal;
            }

            public void setGrandTotal(String grandTotal) {
                this.grandTotal = grandTotal;
            }

            public String getServicePercentage() {
                return servicePercentage;
            }

            public void setServicePercentage(String servicePercentage) {
                this.servicePercentage = servicePercentage;
            }

            public String getServiceAmount() {
                return serviceAmount;
            }

            public void setServiceAmount(String serviceAmount) {
                this.serviceAmount = serviceAmount;
            }

            public String getPayableAmount() {
                return payableAmount;
            }

            public void setPayableAmount(String payableAmount) {
                this.payableAmount = payableAmount;
            }
        }


        public static class ReceiptDataBean {
            /**
             * fee : {"studentId":"3123","classId":"145","sectionId":"318","streamId":"293","schoolId":"6","sessionYearId":"3","total":"3700.00"}
             * feesMeta : [[{"cycle_id":1,"cycle_no":1,"fee":"3600.00","transport_fee":"100.00","discount":"0.00","extra":"0.00","total":"3700.00","paid":"3700.00","balance":"0.00"}]]
             * feeCycleMeta : [{"cycle_id":1,"discount":"0.00","extra":"0.00","total":"3700.00","paid":"3700.00","balance":"0.00"}]
             */

            private FeeBean fee;
            private List<List<FeesMetaBean>> feesMeta;
            private List<FeeCycleMetaBean> feeCycleMeta;

            public FeeBean getFee() {
                return fee;
            }

            public void setFee(FeeBean fee) {
                this.fee = fee;
            }

            public List<List<FeesMetaBean>> getFeesMeta() {
                return feesMeta;
            }

            public void setFeesMeta(List<List<FeesMetaBean>> feesMeta) {
                this.feesMeta = feesMeta;
            }

            public List<FeeCycleMetaBean> getFeeCycleMeta() {
                return feeCycleMeta;
            }

            public void setFeeCycleMeta(List<FeeCycleMetaBean> feeCycleMeta) {
                this.feeCycleMeta = feeCycleMeta;
            }

            public static class FeeBean {
                /**
                 * studentId : 3123
                 * classId : 145
                 * sectionId : 318
                 * streamId : 293
                 * schoolId : 6
                 * sessionYearId : 3
                 * total : 3700.00
                 */

                private String studentId;
                private String classId;
                private String sectionId;
                private String streamId;
                private String schoolId;
                private String sessionYearId;
                private String total;

                public String getStudentId() {
                    return studentId;
                }

                public void setStudentId(String studentId) {
                    this.studentId = studentId;
                }

                public String getClassId() {
                    return classId;
                }

                public void setClassId(String classId) {
                    this.classId = classId;
                }

                public String getSectionId() {
                    return sectionId;
                }

                public void setSectionId(String sectionId) {
                    this.sectionId = sectionId;
                }

                public String getStreamId() {
                    return streamId;
                }

                public void setStreamId(String streamId) {
                    this.streamId = streamId;
                }

                public String getSchoolId() {
                    return schoolId;
                }

                public void setSchoolId(String schoolId) {
                    this.schoolId = schoolId;
                }

                public String getSessionYearId() {
                    return sessionYearId;
                }

                public void setSessionYearId(String sessionYearId) {
                    this.sessionYearId = sessionYearId;
                }

                public String getTotal() {
                    return total;
                }

                public void setTotal(String total) {
                    this.total = total;
                }
            }

            public static class FeesMetaBean {
                /**
                 * cycle_id : 1
                 * cycle_no : 1
                 * fee : 3600.00
                 * transport_fee : 100.00
                 * discount : 0.00
                 * extra : 0.00
                 * total : 3700.00
                 * paid : 3700.00
                 * balance : 0.00
                 */

                private int cycle_id;
                private int cycle_no;
                private String fee;
                private String transport_fee;
                private String discount;
                private String extra;
                private String total;
                private String paid;
                private String balance;

                public int getCycle_id() {
                    return cycle_id;
                }

                public void setCycle_id(int cycle_id) {
                    this.cycle_id = cycle_id;
                }

                public int getCycle_no() {
                    return cycle_no;
                }

                public void setCycle_no(int cycle_no) {
                    this.cycle_no = cycle_no;
                }

                public String getFee() {
                    return fee;
                }

                public void setFee(String fee) {
                    this.fee = fee;
                }

                public String getTransport_fee() {
                    return transport_fee;
                }

                public void setTransport_fee(String transport_fee) {
                    this.transport_fee = transport_fee;
                }

                public String getDiscount() {
                    return discount;
                }

                public void setDiscount(String discount) {
                    this.discount = discount;
                }

                public String getExtra() {
                    return extra;
                }

                public void setExtra(String extra) {
                    this.extra = extra;
                }

                public String getTotal() {
                    return total;
                }

                public void setTotal(String total) {
                    this.total = total;
                }

                public String getPaid() {
                    return paid;
                }

                public void setPaid(String paid) {
                    this.paid = paid;
                }

                public String getBalance() {
                    return balance;
                }

                public void setBalance(String balance) {
                    this.balance = balance;
                }
            }

            public static class FeeCycleMetaBean {
                /**
                 * cycle_id : 1
                 * discount : 0.00
                 * extra : 0.00
                 * total : 3700.00
                 * paid : 3700.00
                 * balance : 0.00
                 */

                private int cycle_id;
                private String discount;
                private String extra;
                private String total;
                private String paid;
                private String balance;

                public int getCycle_id() {
                    return cycle_id;
                }

                public void setCycle_id(int cycle_id) {
                    this.cycle_id = cycle_id;
                }

                public String getDiscount() {
                    return discount;
                }

                public void setDiscount(String discount) {
                    this.discount = discount;
                }

                public String getExtra() {
                    return extra;
                }

                public void setExtra(String extra) {
                    this.extra = extra;
                }

                public String getTotal() {
                    return total;
                }

                public void setTotal(String total) {
                    this.total = total;
                }

                public String getPaid() {
                    return paid;
                }

                public void setPaid(String paid) {
                    this.paid = paid;
                }

                public String getBalance() {
                    return balance;
                }

                public void setBalance(String balance) {
                    this.balance = balance;
                }
            }
        }

        public static class FeesBean {
            /**
             * headerName : Tution
             * cycle : Monthy
             * amount : 1100.00
             */

            private int rowType = 1;
            private String headerName;
            private String cycle;
            private String amount;

            public String getHeaderName() {
                return headerName;
            }

            public void setHeaderName(String headerName) {
                this.headerName = headerName;
            }

            public String getCycle() {
                return cycle;
            }

            public void setCycle(String cycle) {
                this.cycle = cycle;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public int getRowType() {
                return rowType;
            }

            public void setRowType(int rowType) {
                this.rowType = rowType;
            }
        }
    }
}
